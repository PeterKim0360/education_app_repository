package com.zjxu.educationapp.modules.service;

import com.zjxu.educationapp.modules.vo.ChatRequest;
import com.zjxu.educationapp.modules.vo.ChatResponse;

public interface CelebrityChatService {
    ChatResponse chatWithCelebrity(ChatRequest request);
}