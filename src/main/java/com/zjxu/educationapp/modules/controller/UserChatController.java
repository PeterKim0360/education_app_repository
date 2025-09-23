package com.zjxu.educationapp.modules.controller;

import com.zjxu.educationapp.common.utils.Result;
import com.zjxu.educationapp.modules.service.UserChatService;
import com.zjxu.educationapp.modules.vo.ChatHistoryRequest;
import com.zjxu.educationapp.modules.vo.ChatHistoryResponse;
import com.zjxu.educationapp.modules.vo.SendMessageRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 用户聊天相关接口
 */
@Slf4j
@RestController
@Tag(name = "用户聊天相关接口")
@RequestMapping("/api/userChat")
public class UserChatController {

    @Autowired
    private UserChatService userChatService;

    /**
     * 查询聊天历史记录
     * @param request 查询请求参数
     * @return 聊天历史响应
     */
    @Operation(summary = "查询聊天历史记录", description = "分页查询当前用户与指定用户的聊天历史记录")
    @PostMapping("/history")
    public Result<ChatHistoryResponse> getChatHistory(@RequestBody ChatHistoryRequest request) {
        try {
            ChatHistoryResponse response = userChatService.getChatHistory(request);
            return Result.ok(response);
        } catch (Exception e) {
            log.error("查询聊天历史失败", e);
            return Result.error("查询聊天历史失败：" + e.getMessage());
        }
    }

    /**
     * 查询聊天历史记录（GET方式）
     * @param otherUserId 对方用户ID
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 聊天历史响应
     */
    @Operation(summary = "查询聊天历史记录", description = "GET方式分页查询当前用户与指定用户的聊天历史记录")
    @GetMapping("/history")
    public Result<ChatHistoryResponse> getChatHistory(
            @RequestParam("otherUserId") Long otherUserId,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize) {
        try {
            ChatHistoryRequest request = new ChatHistoryRequest();
            request.setOtherUserId(otherUserId);
            request.setPageNum(pageNum);
            request.setPageSize(pageSize);
            
            ChatHistoryResponse response = userChatService.getChatHistory(request);
            return Result.ok(response);
        } catch (Exception e) {
            log.error("查询聊天历史失败", e);
            return Result.error("查询聊天历史失败：" + e.getMessage());
        }
    }

    /**
     * 保存聊天消息（HTTP方式）
     * @param request 发送消息请求
     * @return 消息ID
     */
    @Operation(summary = "保存聊天消息", description = "通过HTTP方式保存聊天消息")
    @PostMapping("/send")
    public Result<Long> sendMessage(@RequestBody SendMessageRequest request) {
        try {
            Long messageId = userChatService.saveMessage(request);
            return Result.ok(messageId);
        } catch (Exception e) {
            log.error("发送消息失败", e);
            return Result.error("发送消息失败：" + e.getMessage());
        }
    }

    /**
     * 标记消息为已读
     * @param fromUserId 发送者ID
     * @return 更新的消息数量
     */
    @Operation(summary = "标记消息为已读", description = "将指定发送者发给当前用户的未读消息标记为已读")
    @PostMapping("/markAsRead")
    public Result<Integer> markMessagesAsRead(@RequestParam("fromUserId") Long fromUserId) {
        try {
            // 获取当前用户ID作为接收者
            cn.dev33.satoken.stp.StpUtil.checkLogin();
            Long currentUserId = cn.dev33.satoken.stp.StpUtil.getLoginIdAsLong();
            
            Integer count = userChatService.markMessagesAsRead(fromUserId, currentUserId);
            return Result.ok(count);
        } catch (Exception e) {
            log.error("标记消息已读失败", e);
            return Result.error("标记消息已读失败：" + e.getMessage());
        }
    }
} 