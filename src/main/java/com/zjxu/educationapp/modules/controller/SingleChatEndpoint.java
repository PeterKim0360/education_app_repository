package com.zjxu.educationapp.modules.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.alibaba.fastjson2.JSONObject;
import com.zjxu.educationapp.common.config.WebSocketConfigurator;
import com.zjxu.educationapp.common.constant.ErrorCode;
import com.zjxu.educationapp.common.constant.SecurityConstant;
import com.zjxu.educationapp.common.utils.Result;
import com.zjxu.educationapp.modules.service.UserService;
import com.zjxu.educationapp.modules.service.UserChatService;
import com.zjxu.educationapp.modules.vo.SendMessageRequestVO;
import com.zjxu.educationapp.modules.vo.WebSocketMessageVO;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;


import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
//请求路径为 ws://localhost:8080/single/chat
@ServerEndpoint(value = "/single/chat/{toUserId}", configurator = WebSocketConfigurator.class)
public class SingleChatEndpoint {
    /*
    此处@Component作用：Spring扫描到当前类，然后创建被容器管理的端点对象，通过setter方法对类对象进行赋值，这样即使是不归spring管理的端点对象也可以使用这个静态类对象
    当前类的实例由Tomcat容器创建，无法对非Spring管理的bean对象进行自动注入
    这个端点对象不是单例的，每此ws连接对应全新的端点实例
     */
    private static UserService userService;
    private static UserChatService userChatService;

    //通过发送人->收件人组合id 映射到对应会话
    private final static ConcurrentHashMap<String, Session> onlineUserSession = new ConcurrentHashMap<>();

    //此处使用成员变量在并发情况下不会出现被其它线程修改问题，因为当前类是多例的
    private String sessionKey;
    private String reverseSessionKey;
    
    //另一种可用注入方式
    @Autowired
    public void setUserService(UserService userService) {
        SingleChatEndpoint.userService = userService;
    }

    @Autowired
    public void setUserChatService(UserChatService userChatService) {
        SingleChatEndpoint.userChatService = userChatService;
    }

    //NOTE 区别于springMvc，websocket协议需要使用@PathParm("参数名")来获取路径参数
    @OnOpen
    public void onOpen(@PathParam("toUserId") Long toUserId, Session session) {
        log.info("WebSocket连接建立，toUserId: {}", toUserId);
//        log.info("当前端点地址：{}",this);
        /*
        建立连接时就确定接收消息人，在线or不在线：在线通过websocket发消息，不在线调用service持久化历史消息；
        一开始进入对话页面需要将历史消息展示出来，能够像微信一样确定是谁发的历史消息
        1.进入对话框发送两个请求：websocket请求和获取历史消息http请求
        2.websocket采用双向连接，toId->fromId 表示接收人对发送人建立会话连接，反之同理
         */
        Object authError = session.getUserProperties().get(SecurityConstant.AUTH_ERROR);
        if (authError != null) {
            //token认证失败
            sendAuthErrorAndClose(session, authError);
            return;
        }
        long fromUserId = Long.parseLong(session.getUserProperties().get(SecurityConstant.USER_ID).toString());
        //id组合key
        this.sessionKey = fromUserId + ":" + toUserId;
        this.reverseSessionKey = toUserId + ":" + fromUserId;

        onlineUserSession.put(sessionKey, session);
        log.info("WebSocket连接成功，sessionKey: {}", sessionKey);
    }

    //NOTE 此注解规范了参数有String:message和Session:session，因此如果需要传入自定义JSON对象时，只能手动解析message
    @OnMessage
    public void onMessage(@PathParam("toUserId") Long toUserId, String message, Session session) {
        log.info("接收到消息：{}, toUserId: {}", message, toUserId);

        try {
            // 获取发送者ID
            long fromUserId = Long.parseLong(session.getUserProperties().get(SecurityConstant.USER_ID).toString());
            
            // 解析消息内容和类型
            WebSocketMessageVO messageInfo = parseMessage(message);
            
            // 保存消息到数据库
            Long messageId = saveMessageToDatabase(fromUserId, toUserId, messageInfo);
            
            // 构建响应消息（包含消息ID和发送时间等信息）
            WebSocketMessageVO responseMessage = WebSocketMessageVO.builder()
                    .content(messageInfo.getContent())
                    .messageType(messageInfo.getMessageType())
                    .messageId(messageId)
                    .timestamp(System.currentTimeMillis())
                    .status("success")
                    .build();
            
            // 实时发送消息
            sendRealTimeMessage(JSONObject.toJSONString(responseMessage), session);
        } catch (IOException e) {
            log.error("发送消息失败，onMessage()", e);
            // 发送错误消息给客户端
            sendErrorMessage(session, "发送消息失败");
        } catch (Exception e) {
            log.error("处理消息失败", e);
            // 发送错误消息给客户端
            sendErrorMessage(session, "处理消息失败: " + e.getMessage());
        }
    }

    @OnClose
    public void onClose(@PathParam("toUserId") Long toUserId) {
        //此时自动关闭连接
        // 检查sessionKey是否为null
        if (sessionKey != null) {
            onlineUserSession.remove(sessionKey);
            log.info("WebSocket连接关闭，sessionKey: {}", sessionKey);
        } else {
            log.warn("WebSocket连接关闭时sessionKey为null");
        }
    }

    @OnError
    public void onError(Session session, Throwable e) {
        log.error("websocket连接异常：{}",e.getMessage());
        try {
            session.close(new CloseReason(CloseReason.CloseCodes.UNEXPECTED_CONDITION, e.getMessage()));
        } catch (IOException IOe) {
            log.error("websocket会话关闭失败：{}",IOe.getMessage());
        }
    }

    private void sendAuthErrorAndClose(Session session, Object authError) {
        try {
            session.getBasicRemote().sendText(JSONObject.toJSONString(Result.error(ErrorCode.UNKNOWN_LOGIN_ERROR)));
            if(session.isOpen()){
                session.close(new CloseReason(CloseReason.CloseCodes.VIOLATED_POLICY, authError.toString()));
            }
        } catch (IOException e) {
            log.error("websocket发送错误信息失败");
            try {
                session.close();
            } catch (IOException ex) {
                log.error("websocket会话关闭失败");
            }
        }
    }

    private void sendRealTimeMessage(String message, Session session) throws IOException {
        if(onlineUserSession.containsKey(reverseSessionKey)){
            //在线，实时发送消息
            log.info("对方在线，实时发送消息");
            Session reverseSession = onlineUserSession.get(reverseSessionKey);
            reverseSession.getBasicRemote().sendText(message);
        }else{
            log.info("对方不在线，消息已保存到数据库");
            // 消息已经在 onMessage 方法中保存到数据库了
        }
    }

    /**
     * 保存消息到数据库
     * @param fromUserId 发送者ID
     * @param toUserId 接收者ID
     * @param messageInfo 消息信息
     * @return 消息ID
     */
    private Long saveMessageToDatabase(Long fromUserId, Long toUserId, WebSocketMessageVO messageInfo) {
        try {
            if (userChatService != null) {
                SendMessageRequestVO request = new SendMessageRequestVO();
                request.setToUserId(toUserId);
                request.setContent(messageInfo.getContent());
                request.setMessageType(messageInfo.getMessageType());
                
                // 使用重载方法直接传递发送者ID
                Long messageId = userChatService.saveMessage(fromUserId, request);
                log.info("消息保存成功，messageId: {}", messageId);
                return messageId;
            } else {
                log.warn("UserChatService未注入，无法保存消息");
                return null;
            }
        } catch (Exception e) {
            log.error("保存消息到数据库失败", e);
            throw new RuntimeException("保存消息失败", e);
        }
    }

    /**
     * 解析WebSocket消息
     * @param message 原始消息
     * @return 解析后的消息信息
     */
    private WebSocketMessageVO parseMessage(String message) {
        try {
            // 尝试解析为JSON对象
            WebSocketMessageVO messageInfo = JSONObject.parseObject(message, WebSocketMessageVO.class);
            if (messageInfo != null && messageInfo.getContent() != null) {
                // 设置默认值
                if (messageInfo.getMessageType() == null) {
                    messageInfo.setMessageType(1); // 默认文字消息
                }
                return messageInfo;
            }
        } catch (Exception e) {
            log.debug("消息不是JSON格式，当作普通文本处理: {}", message);
        }
        
        // 如果不是JSON格式，当作普通文字消息处理
        return WebSocketMessageVO.builder()
                .content(message)
                .messageType(1)
                .build();
    }

    /**
     * 发送错误消息给客户端
     * @param session WebSocket会话
     * @param errorMsg 错误消息
     */
    private void sendErrorMessage(Session session, String errorMsg) {
        try {
            WebSocketMessageVO errorMessage = WebSocketMessageVO.builder()
                    .status("error")
                    .errorMsg(errorMsg)
                    .timestamp(System.currentTimeMillis())
                    .build();
            session.getBasicRemote().sendText(JSONObject.toJSONString(errorMessage));
        } catch (IOException e) {
            log.error("发送错误消息失败", e);
        }
    }

    /*
    // 常见的关闭代码：
        CloseReason.CloseCodes.NORMAL_CLOSURE          // 1000 - 正常关闭
        CloseReason.CloseCodes.GOING_AWAY              // 1001 - 端点离开
        CloseReason.CloseCodes.PROTOCOL_ERROR          // 1002 - 协议错误
        CloseReason.CloseCodes.CANNOT_ACCEPT           // 1003 - 无法接受的数据类型
        CloseReason.CloseCodes.NO_STATUS_CODE          // 1005 - 没有状态码
        CloseReason.CloseCodes.CLOSED_ABNORMALLY       // 1006 - 异常关闭
        CloseReason.CloseCodes.NOT_CONSISTENT          // 1007 - 数据不一致
        CloseReason.CloseCodes.VIOLATED_POLICY         // 1008 - 违反政策
        CloseReason.CloseCodes.TOO_BIG                 // 1009 - 消息太大
        CloseReason.CloseCodes.NO_EXTENSION            // 1010 - 缺少扩展
        CloseReason.CloseCodes.UNEXPECTED_CONDITION    // 1011 - 意外情况
        CloseReason.CloseCodes.SERVICE_RESTART         // 1012 - 服务重启
        CloseReason.CloseCodes.TRY_AGAIN_LATER         // 1013 - 稍后重试
        CloseReason.CloseCodes.BAD_GATEWAY             // 1014 - 错误网关
        CloseReason.CloseCodes.TLS_HANDSHAKE_FAILURE   // 1015 - TLS握手失败
     */
}
