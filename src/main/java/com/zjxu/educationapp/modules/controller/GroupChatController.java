package com.zjxu.educationapp.modules.controller;

import com.alibaba.fastjson.JSONObject;
import com.zjxu.educationapp.common.utils.Result;
import com.zjxu.educationapp.modules.dto.GroupChatMessageByTeachDTO;
import com.zjxu.educationapp.modules.dto.GroupChatMessageDTO;
import com.zjxu.educationapp.modules.entity.GroupChatMessage;
import com.zjxu.educationapp.modules.entity.GroupTeam;
import com.zjxu.educationapp.modules.entity.GroupTeamMember;
import com.zjxu.educationapp.modules.service.GroupChatService;
import com.zjxu.educationapp.modules.service.GroupTeamMemberService;
import com.zjxu.educationapp.modules.service.GroupTeamService;
import com.zjxu.educationapp.modules.vo.GroupChatHistoryResponseVO;
import com.zjxu.educationapp.modules.vo.WebSocketMessageVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

/**
 * 小组讨论区
 */
@RestController
@RequestMapping("/groupChat")
@Slf4j
@Tag(name = "小组讨论区")
public class GroupChatController {
    @Autowired
    private GroupChatService groupChatService;


    /**
     * 获取小组历史消息
     */
    @GetMapping("/getHistory")
    @Operation(summary = "获取小组历史消息")
    public Result<GroupChatHistoryResponseVO> getHistory(@RequestParam("teamId") Long teamId,
                                                         @RequestParam(defaultValue = "1") Integer pageNum,
                                                         @RequestParam(defaultValue = "20") Integer pageSize) {
        log.info("获取小组历史消息");
        return groupChatService.getHistory(teamId, pageNum, pageSize);
    }

    /**
     * 保存小组聊天消息
     */
    @PostMapping("/saveMessage")
    @Operation(summary = "保存小组聊天消息")
    public Result<Long> saveMessage(@RequestBody GroupChatMessageDTO messageDTO) {
        log.info("保存小组聊天消息，teamId:{},fromUserId:{}", messageDTO.getTeamId(), messageDTO.getFromUserId());
        Long messageId = groupChatService.saveMessage(messageDTO);
        return Result.ok(messageId);
    }

    /**
     * 组内发送消息
     */
    @PostMapping("/sendMessage")
    @Operation(summary = "发送消息", description = "传参: messageDTO")
    public Result<Long> sendMessage(@RequestBody GroupChatMessageDTO messageDTO) {
        log.info("发送消息，teamId:{},fromUserId:{}", messageDTO.getTeamId(), messageDTO.getFromUserId());
        Long messageId = groupChatService.saveMessage(messageDTO);
        //构造WS消息
        WebSocketMessageVO message = WebSocketMessageVO.builder()
                .content(messageDTO.getContent())
                .messageType(messageDTO.getMessageType())
                .messageId(messageId)
                .timestamp(System.currentTimeMillis())
                .status("success")
                .build();
        try {
            GroupChatEndpoint.broadcastToRoom(messageDTO.getTeamId(), JSONObject.toJSONString(message));
        } catch (IOException e) {
            log.error("发送消息失败", e);
        }
        return Result.ok(messageId);
    }

    /**
     * 老师统一广播给每个小组
     */
    @PostMapping("/broadcast")
    @Operation(summary = "老师统一广播给每个小组")
    public Result<List<Long>> broadcast(@RequestBody GroupChatMessageByTeachDTO messageByTeachDTO) {
        log.info("老师统一广播给每个小组");
        List<Long> messageIds=groupChatService.saveAllMessage(messageByTeachDTO);
        log.info("messageIds:{}", messageIds);
        WebSocketMessageVO message = WebSocketMessageVO.builder()
                .content(messageByTeachDTO.getContent())
                .messageIds(messageIds)
                .messageType(messageByTeachDTO.getMessageType())
                .timestamp(System.currentTimeMillis())
                .status("success")
                .build();
        try {
            GroupChatEndpoint.broadcastToAllGroups(JSONObject.toJSONString(message));
        } catch (IOException e) {
            log.error("发送消息失败", e);
        }
        return Result.ok(messageIds);
    }

    /**
     * 删除当前全部小组
     */
    @DeleteMapping("/deleteAll")
    @Operation(summary = "删除当前全部小组")
    public Result<?> deleteAll(Integer subjectId,Long createdBy) {
        log.info("删除当前全部小组");
        return groupChatService.deleteAll(subjectId,createdBy);
    }

}
