package com.zjxu.educationapp.modules.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zjxu.educationapp.modules.entity.UserChatMessage;
import com.zjxu.educationapp.modules.entity.UserEntity;
import com.zjxu.educationapp.modules.mapper.UserChatMessageMapper;
import com.zjxu.educationapp.modules.mapper.UserMapper;
import com.zjxu.educationapp.modules.service.UserChatService;
import com.zjxu.educationapp.modules.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户聊天服务实现类
 */
@Slf4j
@Service
public class UserChatServiceImpl implements UserChatService {

    @Autowired
    private UserChatMessageMapper userChatMessageMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public ChatHistoryResponseVO getChatHistory(ChatHistoryRequestVO request) {
        // 获取当前登录用户ID
        Long currentUserId = StpUtil.getLoginIdAsLong();

        // 生成会话ID
        String conversationId = generateConversationId(currentUserId, request.getOtherUserId());

        // 计算分页参数
        Integer offset = (request.getPageNum() - 1) * request.getPageSize();

        // 查询聊天记录
        List<UserChatMessage> messages = userChatMessageMapper.selectByConversationIdWithPaging(
                conversationId, offset, request.getPageSize());

        // 统计总数
        Long total = userChatMessageMapper.countByConversationId(conversationId);

        // 获取对方用户信息
        UserEntity otherUser = userMapper.selectById(request.getOtherUserId());
        if (otherUser == null) {
            throw new RuntimeException("对方用户不存在");
        }

        // 转换为VO
        List<UserChatMessageVO> messageVOs = messages.stream().map(message -> {
            UserEntity fromUser = userMapper.selectById(message.getFromUserId());
            UserEntity toUser = userMapper.selectById(message.getToUserId());

            return UserChatMessageVO.builder()
                    .id(message.getId())
                    .fromUserId(message.getFromUserId())
                    .fromUserName(fromUser != null ? fromUser.getUserName() : "未知用户")
                    .fromUserAvatar(fromUser != null ? fromUser.getAvatarUrl() : "")
                    .toUserId(message.getToUserId())
                    .toUserName(toUser != null ? toUser.getUserName() : "未知用户")
                    .toUserAvatar(toUser != null ? toUser.getAvatarUrl() : "")
                    .content(message.getContent())
                    .messageType(message.getMessageType())
                    .status(message.getStatus())
                    .sendTime(message.getSendTime())
                    .isSentByCurrentUser(message.getFromUserId().equals(currentUserId))
                    .build();
        }).collect(Collectors.toList());

        // 计算分页信息
        Integer totalPages = (int) Math.ceil((double) total / request.getPageSize());
        Boolean hasMore = request.getPageNum() < totalPages;

        return ChatHistoryResponseVO.builder()
                .messages(messageVOs)
                .pageNum(request.getPageNum())
                .pageSize(request.getPageSize())
                .total(total)
                .totalPages(totalPages)
                .hasMore(hasMore)
                .otherUser(ChatHistoryResponseVO.UserInfoVO.builder()
                        .userId(otherUser.getId())
                        .userName(otherUser.getUserName())
                        .avatarUrl(otherUser.getAvatarUrl())
                        .identity(otherUser.getIdentity())
                        .build())
                .build();
    }

    @Override
    public Long saveMessage(SendMessageRequestVO request) {
        // 获取当前登录用户ID
        Long currentUserId = StpUtil.getLoginIdAsLong();
        return saveMessage(currentUserId, request);
    }

    @Override
    public Long saveMessage(Long fromUserId, SendMessageRequestVO request) {
        // 生成会话ID
        String conversationId = generateConversationId(fromUserId, request.getToUserId());

        // 构建消息对象
        UserChatMessage message = UserChatMessage.builder()
                .fromUserId(fromUserId)
                .toUserId(request.getToUserId())
                .content(request.getContent())
                .messageType(request.getMessageType())
                .status(0) // 默认未读
                .sendTime(new Date())
                .conversationId(conversationId)
                .build();

        // 保存到数据库
        userChatMessageMapper.insert(message);

        return message.getId();
    }

    @Override
    public Integer markMessagesAsRead(Long fromUserId, Long toUserId) {
        return userChatMessageMapper.markMessagesAsRead(fromUserId, toUserId);
    }

    @Override
    public String generateConversationId(Long userId1, Long userId2) {
        // 确保较小的ID在前，较大的ID在后，保证会话ID的唯一性
        if (userId1.compareTo(userId2) < 0) {
            return userId1 + ":" + userId2;
        } else {
            return userId2 + ":" + userId1;
        }
    }
}