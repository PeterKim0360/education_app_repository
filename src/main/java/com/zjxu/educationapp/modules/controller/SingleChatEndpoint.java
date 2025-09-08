package com.zjxu.educationapp.modules.controller;

import com.alibaba.fastjson2.JSONObject;
import com.zjxu.educationapp.common.config.WebSocketConfigurator;
import com.zjxu.educationapp.common.constant.ErrorCode;
import com.zjxu.educationapp.common.constant.SecurityConstant;
import com.zjxu.educationapp.common.utils.Result;
import com.zjxu.educationapp.modules.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
    当前类的实例有Tomcat容器创建，因此即使标注了@Component也不被Spring容器管理，因此不能直接使用@Autowired标注在字段上（静态字段不能也不能使用此注解）
     */
    //TODO 修改Service
    private static UserService userService;

    //通过发送人->收件人组合id 映射到对应会话
    private static ConcurrentHashMap<String, Session> onlineUserSession = new ConcurrentHashMap<>();

//    private Long fromUserId;

    //TODO 关于多用户的会话，单例问题
    private String sessionKey;
    private String reverseSessionKey;
    //另一种可用注入方式
    @Autowired
    public void setUserService(UserService userService) {
        SingleChatEndpoint.userService = userService;
    }

    //NOTE 区别于springMvc，websocket协议需要使用@PathParm("参数名")来获取路径参数
    @OnOpen
    public void onOpen(@PathParam("toUserId") Long toUserId, Session session) {
        log.info("");
        /*
        建立连接时就确定接收消息人，在线or不在线：在线通过websocket发消息，不在线调用service持久化历史消息；
        一开始进入对话页面需要将历史消息展示出来，能够像微信一样确定是谁发的历史消息
        1.进入对话框发送两个请求：websocket请求和获取历史消息http请求
        2.websocket采用双向连接，toId->fromId 表示接收人对发送人建立会话连接，反之同理
         */
        //TODO 未做历史消息持久化模块
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
    }

    //NOTE 此注解规范了参数有String:message和Session:session，因此如果需要传入自定义JSON对象时，只能手动解析message
    @OnMessage
    public void onMessage(@PathParam("toUserId") Long toUserId,String message, Session session) {
        System.out.println("接收到消息：" + message);

        try {
            sendRealTimeMessage(message, session);
        } catch (IOException e) {
            log.info("发送消息失败，onMessage()");
        }
    }

    @OnClose
    public void onClose(@PathParam("toUserId") Long toUserId) {
        //此时自动关闭连接
        onlineUserSession.remove(sessionKey);
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
            session.close(new CloseReason(CloseReason.CloseCodes.VIOLATED_POLICY, authError.toString()));
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
            log.info("在线");
            Session reverseSession = onlineUserSession.get(reverseSessionKey);
            reverseSession.getBasicRemote().sendText(message);
        }else{
            log.info("不在线");
            //不在线，将历史消息持久化
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
