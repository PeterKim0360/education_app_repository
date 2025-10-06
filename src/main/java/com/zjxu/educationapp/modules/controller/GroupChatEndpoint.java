package com.zjxu.educationapp.modules.controller;

import cn.hutool.core.util.ObjUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zjxu.educationapp.common.config.WebSocketConfigurator;
import com.zjxu.educationapp.common.constant.ErrorCode;
import com.zjxu.educationapp.common.constant.SecurityConstant;
import com.zjxu.educationapp.common.utils.Result;
import com.zjxu.educationapp.modules.dto.GroupChatMessageDTO;
import com.zjxu.educationapp.modules.entity.GroupTeam;
import com.zjxu.educationapp.modules.entity.GroupTeamMember;
import com.zjxu.educationapp.modules.mapper.GroupTeamMapper;
import com.zjxu.educationapp.modules.mapper.GroupTeamMemberMapper;
import com.zjxu.educationapp.modules.service.GroupChatService;
import com.zjxu.educationapp.modules.vo.WebSocketMessageVO;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static io.lettuce.core.pubsub.PubSubOutput.Type.message;

@Slf4j
@Component
@ServerEndpoint(value = "/group/chat/{teamId}", configurator = WebSocketConfigurator.class)
public class GroupChatEndpoint {

    private static final Map<Long, Map<String, Session>> ROOM_SESSIONS = new ConcurrentHashMap<>();

    private static GroupTeamMapper groupTeamMapper;
    private static GroupTeamMemberMapper groupTeamMemberMapper;
    private static GroupChatService groupChatService;

    private Long currentTeamId;
    private Long currentUserId;

    @Autowired
    public void setGroupTeamMapper(GroupTeamMapper mapper) {
        GroupChatEndpoint.groupTeamMapper = mapper;
    }

    @Autowired
    public void setGroupTeamMemberMapper(GroupTeamMemberMapper mapper) {
        GroupChatEndpoint.groupTeamMemberMapper = mapper;
    }

    @Autowired
    public void setGroupChatService(GroupChatService service) {
        GroupChatEndpoint.groupChatService = service;
    }

    @OnOpen
    public void onOpen(@PathParam("teamId") Long teamId, Session session) {
        Object authError = session.getUserProperties().get(SecurityConstant.AUTH_ERROR);
        if (authError != null) {
            sendAuthErrorAndClose(session, authError);
            return;
        }

        this.currentTeamId = teamId;
        this.currentUserId = Long.parseLong(session.getUserProperties().get(SecurityConstant.USER_ID).toString());

        if (!isAuthorized(teamId, currentUserId)) {
            sendAuthErrorAndClose(session, "没有权限进入该组聊天室");
            return;
        }

        ROOM_SESSIONS.computeIfAbsent(teamId, k -> new ConcurrentHashMap<>())
                .put(session.getId(), session);
    }

    @OnMessage
    public void onMessage(@PathParam("teamId") Long teamId, String message, Session session) {
        try {
            WebSocketMessageVO messageInfo = parseMessage(message);
            GroupChatMessageDTO groupChatMessageDTO = new GroupChatMessageDTO();
            groupChatMessageDTO.setTeamId(teamId);
            groupChatMessageDTO.setFromUserId(currentUserId);
            groupChatMessageDTO.setContent(messageInfo.getContent());
            groupChatMessageDTO.setMessageType(messageInfo.getMessageType());
            Long messageId = groupChatService.saveMessage(groupChatMessageDTO);

            WebSocketMessageVO response = WebSocketMessageVO.builder()
                    .content(messageInfo.getContent())
                    .messageType(messageInfo.getMessageType())
                    .messageId(messageId)
                    .timestamp(System.currentTimeMillis())
                    .status("success")
                    .build();
            broadcastToRoom(teamId, JSONObject.toJSONString(response));
        } catch (Exception e) {
            sendErrorMessage(session, "发送消息失败: " + e.getMessage());
        }
    }

    @OnClose
    public void onClose(@PathParam("teamId") Long teamId, Session session) {
        Map<String, Session> sessions = ROOM_SESSIONS.get(teamId);
        if (sessions != null) {
            sessions.remove(session.getId());
            if (sessions.isEmpty()) {
                ROOM_SESSIONS.remove(teamId);
            }
        }
    }

    @OnError
    public void onError(Session session, Throwable e) {
        log.error("Group WS error: {}", e.getMessage());
        try {
            session.close(new CloseReason(CloseReason.CloseCodes.UNEXPECTED_CONDITION, e.getMessage()));
        } catch (IOException ignored) {}
    }

    private boolean isAuthorized(Long teamId, Long userId) {
        GroupTeam team = groupTeamMapper.selectById(teamId);
        if (team == null) { return false; }
        //教师可以随意进入
        if (Objects.equals(userId, team.getCreatedBy())){
            return true;
        }
        Long count = groupTeamMemberMapper.selectCount(new LambdaQueryWrapper<GroupTeamMember>()
                .eq(GroupTeamMember::getTeamId, teamId)
                .eq(GroupTeamMember::getUserId, userId)
                .eq(GroupTeamMember::getStatus, 1));
        if (ObjUtil.isEmpty(count)){
            //查看是不是教师
            count  = groupTeamMapper.selectCount(new LambdaQueryWrapper<GroupTeam>()
                    .eq(GroupTeam::getCreatedBy, userId)
                    .eq(GroupTeam::getId, teamId)
                    .eq(GroupTeam::getStatus, 1));
        }

        return count != null && count > 0;
    }

    /**
     * 组内广播
     * @param teamId
     * @param text
     * @throws IOException
     */
    public static void broadcastToRoom(Long teamId, String text) throws IOException {
        Map<String, Session> sessions = ROOM_SESSIONS.get(teamId);
        if (sessions == null) { return; }
        for (Session s : sessions.values()) {
            if (s.isOpen()) {
                s.getBasicRemote().sendText(text);
            }
        }
    }

    /**
     * 老师能发给所有小组
     * @param text
     * @throws IOException
     */
    public static void broadcastToAllGroups(String text) throws IOException {
        for (Long teamId : ROOM_SESSIONS.keySet()) {
            broadcastToRoom(teamId, text);
        }
    }


    private void sendAuthErrorAndClose(Session session, Object authError) {
        try {
            session.getBasicRemote().sendText(JSONObject.toJSONString(Result.error(ErrorCode.UNKNOWN_LOGIN_ERROR)));
            session.close(new CloseReason(CloseReason.CloseCodes.VIOLATED_POLICY, authError.toString()));
        } catch (IOException ignored) { }
    }

    private void sendErrorMessage(Session session, String err) {
        try {
            WebSocketMessageVO response = WebSocketMessageVO.builder()
                    .status("error")
                    .errorMsg(err)
                    .timestamp(System.currentTimeMillis())
                    .build();
            session.getBasicRemote().sendText(JSONObject.toJSONString(response));
        } catch (IOException ignored) { }
    }

    private WebSocketMessageVO parseMessage(String raw) {
        try {
            WebSocketMessageVO vo = JSONObject.parseObject(raw, WebSocketMessageVO.class);
            if (vo.getMessageType() == null) { vo.setMessageType(1); }
            return vo;
        } catch (Exception e) {
            WebSocketMessageVO vo = new WebSocketMessageVO();
            vo.setContent(raw);
            vo.setMessageType(1);
            return vo;
        }
    }
}

