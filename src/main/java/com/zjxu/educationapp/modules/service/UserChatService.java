package com.zjxu.educationapp.modules.service;

import com.zjxu.educationapp.modules.vo.ChatHistoryRequest;
import com.zjxu.educationapp.modules.vo.ChatHistoryResponse;
import com.zjxu.educationapp.modules.vo.SendMessageRequest;

/**
 * 用户聊天服务接口
 */
public interface UserChatService {

    /**
     * 查询聊天历史记录
     * @param request 查询请求
     * @return 聊天历史响应
     */
    ChatHistoryResponse getChatHistory(ChatHistoryRequest request);

    /**
     * 保存聊天消息
     * @param request 发送消息请求
     * @return 消息ID
     */
    Long saveMessage(SendMessageRequest request);

    /**
     * 保存聊天消息（指定发送者）
     * @param fromUserId 发送者ID
     * @param request 发送消息请求
     * @return 消息ID
     */
    Long saveMessage(Long fromUserId, SendMessageRequest request);

    /**
     * 标记消息为已读
     * @param fromUserId 发送者ID
     * @param toUserId 接收者ID（当前用户）
     * @return 更新的消息数量
     */
    Integer markMessagesAsRead(Long fromUserId, Long toUserId);

    /**
     * 生成会话ID
     * @param userId1 用户1 ID
     * @param userId2 用户2 ID
     * @return 会话ID
     */
    String generateConversationId(Long userId1, Long userId2);
} 