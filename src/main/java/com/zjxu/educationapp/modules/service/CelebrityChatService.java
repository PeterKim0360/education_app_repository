package com.zjxu.educationapp.modules.service;

import com.zjxu.educationapp.modules.vo.ChatRequest;
import com.zjxu.educationapp.modules.vo.ChatResponse;

public interface CelebrityChatService {
    ChatResponse chatWithCelebrity(ChatRequest request);

    /**
     * 初始化对话，让AI发送欢迎消息
     * @param celebrityId
     * @return
     */
    ChatResponse initByAI(Long celebrityId);
}