package com.zjxu.educationapp.modules.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zjxu.educationapp.common.utils.Result;
import com.zjxu.educationapp.modules.entity.Celebrity;
import com.zjxu.educationapp.modules.entity.CelebrityChat;
import com.zjxu.educationapp.modules.mapper.CelebrityChatMapper;
import com.zjxu.educationapp.modules.mapper.CelebrityMapper;
import com.zjxu.educationapp.modules.service.CelebrityChatService;
import com.zjxu.educationapp.modules.vo.ChatRequest;
import com.zjxu.educationapp.modules.vo.ChatResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Service
public class CelebrityChatServiceImpl implements CelebrityChatService {

    @Autowired
    private CelebrityMapper celebrityMapper;
    @Autowired
    private CelebrityChatMapper celebrityChatMapper;
    @Autowired
    private AIGCService aigcService;

    @Override
    public ChatResponse chatWithCelebrity(ChatRequest request) {
        // 获取名人信息
        Celebrity celebrity = celebrityMapper.selectOne(new QueryWrapper<Celebrity>()
                .eq("celebrity_id", request.getCelebrityId()));
        if (celebrity == null) {
            throw new RuntimeException("名人不存在");
        }

        // 生成会话ID
        String sessionId = UUID.randomUUID().toString();

        // 保存用户消息到数据库
        saveChatRecord(request.getCelebrityId(), sessionId, request.getContent(), 0);

        // 调用AI生成名人回复
        String aiResponse;
        try {
            aiResponse = aigcService.callAI(celebrity.getAiPrompt(), request.getContent());
        } catch (ApiException | NoApiKeyException | InputRequiredException e) {
            throw new RuntimeException("AI调用失败", e);
        }

        // 保存名人回复到数据库
        saveChatRecord(request.getCelebrityId(), sessionId, aiResponse, 1);

        // 构建返回结果
        ChatResponse response = new ChatResponse();
        response.setContent(aiResponse);
        return response;
    }

    private void saveChatRecord(Long celebrityId, String sessionId, String content, Integer senderType) {
        // 获取当前用户ID，这里假设你有获取当前登录用户ID的工具类方法
        long userId = StpUtil.getLoginIdAsLong();

        CelebrityChat chatRecord = CelebrityChat.builder()
                .userId(userId)
                .celebrityId(celebrityId)
                .sessionId(sessionId)
                .content(content)
                .senderType(senderType)
                .sendTime(new Date())
                .build();
        celebrityChatMapper.insert(chatRecord);
    }
}