package com.zjxu.educationapp.modules.controller;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.zjxu.educationapp.common.config.WebSocketConfigurator;
import com.zjxu.educationapp.common.constant.ErrorCode;
import com.zjxu.educationapp.common.constant.RedisConstant;
import com.zjxu.educationapp.common.constant.SecurityConstant;
import com.zjxu.educationapp.common.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/*
路径为ws://localhost:8080/live/room?roomId=1&identity=0  (identity为1表示老师，为0表示学生)
此接口需要老师一开启直播间就要进行连接
 */
@ServerEndpoint(value = "/live/room", configurator = WebSocketConfigurator.class)
@Slf4j
@Component
public class LiveRoomEndpoint {
    //endpoint不是单例的
    //roomId-> (userId->session)
    private static final ConcurrentHashMap<Integer, ConcurrentHashMap<Long, Session>> roomSessionMap = new ConcurrentHashMap<>();
    //roomId -> teacherId
    private static final ConcurrentHashMap<Integer, Long> roomTeacherIdMap = new ConcurrentHashMap<>();
    private static StringRedisTemplate stringRedisTemplate;

    @Autowired
    public void setStringRedisTemplate(StringRedisTemplate stringRedisTemplate) {
        LiveRoomEndpoint.stringRedisTemplate = stringRedisTemplate;
    }

    @OnOpen
    public void onOpen(Session session) {
        Object authError = session.getUserProperties().get(SecurityConstant.AUTH_ERROR);
        if (authError != null) {
            //token认证失败
            sendAuthErrorAndClose(session, authError);
            return;
        }
        long userId = getUserId(session);
        int roomId = getRoomId(session);
        int identity = getIdentity(session);
        if (identity == 1) {
            roomTeacherIdMap.put(roomId, userId);
            //老师会话存入map中
            roomSessionMap.computeIfAbsent(roomId,k->new ConcurrentHashMap<>()).put(userId, session);
        } else {
            //将学生会话加入到roomSessionMap中
            ConcurrentHashMap<Long, Session> map = roomSessionMap.computeIfAbsent(roomId, k -> new ConcurrentHashMap<>());
            map.put(userId, session);
            String key = RedisConstant.LIVE_ROOM_MESSAGE + roomId;
            Set<ZSetOperations.TypedTuple<String>> messageTuples = stringRedisTemplate.opsForZSet().reverseRangeWithScores(key, 0, -1);
            if (messageTuples != null && !messageTuples.isEmpty()) {
                JSONArray jsonArray = new JSONArray(messageTuples.size());
                for (ZSetOperations.TypedTuple<String> messageTuple : messageTuples) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("message", messageTuple.getValue());
                    jsonObject.put("timestamp", messageTuple.getScore().longValue());
                    jsonArray.add(jsonObject);
                }
                try {
                    session.getBasicRemote().sendText(JSONObject.toJSONString(jsonArray));
                } catch (IOException e) {
                    log.error("发送历史消息失败：{}", e.getMessage());
                }
            }


        }
        log.info("用户{}加入房间{}", userId, roomId);
    }

    private int getIdentity(Session session) {
        return Integer.parseInt(session.getRequestParameterMap().get("identity").get(0));
    }

    private int getRoomId(Session session) {
        return Integer.parseInt(session.getRequestParameterMap().get("roomId").get(0));
    }

    private long getUserId(Session session) {
        Object o = session.getUserProperties().get(SecurityConstant.USER_ID);
        if(o==null){
            return -1L;
        }
        return Long.parseLong(o.toString());
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        try {
            log.info("收到消息：{}", message);
            //老师：发题目给学生；    学生：答题完毕发图片给老师
            int identity = getIdentity(session);
            long userId = getUserId(session);
            int roomId = getRoomId(session);
            if (identity == 1) {
                //1.获取当前房间所有学生的session
                for (Map.Entry<Long, Session> entry : roomSessionMap.get(roomId).entrySet()) {
                    if (entry.getKey() != userId) {
                        try {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("message", message);
                            jsonObject.put("timestamp", System.currentTimeMillis());
                            entry.getValue().getBasicRemote().sendText(jsonObject.toString());
                        } catch (IOException e) {
                            log.error("发送题目给学生失败：{}", e.getMessage());
                        }

                    }
                }
                //2.将题目保存到redis中
                String key = RedisConstant.LIVE_ROOM_MESSAGE + roomId;
                //使用SortedSet结构，保存上传题目的时间和内容
                stringRedisTemplate.opsForZSet().add(key, message, System.currentTimeMillis());
                stringRedisTemplate.expire(key,5, TimeUnit.HOURS);
            } else {
                //学生提交答案发送图片url给老师
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("message", message);
                jsonObject.put("timestamp", System.currentTimeMillis());
                Long teacherId = roomTeacherIdMap.get(roomId);
                ConcurrentHashMap<Long, Session> map = roomSessionMap.get(roomId);
                Session teacherSession = (map == null || teacherId == null) ? null : map.get(teacherId);
                if (teacherSession != null) {
                    try {
                        teacherSession.getBasicRemote().sendText(JSONObject.toJSONString(jsonObject));
                    } catch (IOException e) {
                        log.error("发送图片给老师失败{}", e.getMessage());
                    }
                } else {
                    log.warn("房间{}老师不在线，暂未发送学生消息", roomId);
                }
            }
        } catch (Exception e) {
            log.error("发送消息出现未知异常：{}", e.getMessage());
        }
    }

    @OnClose
    public void onClose(Session session) {
        try {
            long userId = getUserId(session);
            if(userId==-1L){
                return ;
            }
            int roomId = getRoomId(session);
            int identity = getIdentity(session);
            String key = RedisConstant.LIVE_ROOM_MESSAGE + roomId;
            if (identity == 1) {
                roomTeacherIdMap.remove(roomId);
                ConcurrentHashMap<Long, Session> map = roomSessionMap.get(roomId);
                if (map != null) {
                    map.remove(userId);
                    if (map.isEmpty()) {
                        // 没有任何学生在线时再释放房间资源（历史消息保留，已设置过期）
                        roomSessionMap.remove(roomId);
                        stringRedisTemplate.delete(key);
                        log.info("房间{}当前无在线用户，释放房间资源", roomId);
                    }
                }
                log.info("房间{}老师离线", roomId);
            } else {
                ConcurrentHashMap<Long, Session> map = roomSessionMap.get(roomId);
                if (map != null) {
                    map.remove(userId);
                    if (map.isEmpty() && !roomTeacherIdMap.containsKey(roomId)) {
                        // 无学生且老师不在线，释放房间
                        roomSessionMap.remove(roomId);
                        stringRedisTemplate.delete(key);
                        log.info("房间{}无人在线，释放房间资源", roomId);
                    }
                }
            }
        } catch (Exception e) {
            log.error("关闭会话失败：{}", e.getMessage());
        }
    }

    @OnError
    public void onError(Session session, Throwable e) {
        log.error("websocket连接异常：{}", e.getMessage());
        try {
            session.close(new CloseReason(CloseReason.CloseCodes.UNEXPECTED_CONDITION, e.getMessage()));
        } catch (IOException IOe) {
            log.error("websocket会话关闭失败：{}", IOe.getMessage());
        }
    }

    private void sendAuthErrorAndClose(Session session, Object authError) {
        try {
            session.getBasicRemote().sendText(JSONObject.toJSONString(Result.error(ErrorCode.UNKNOWN_LOGIN_ERROR)));
            if (session.isOpen()) {
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
}

