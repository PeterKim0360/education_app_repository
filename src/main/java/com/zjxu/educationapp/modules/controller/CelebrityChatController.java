package com.zjxu.educationapp.modules.controller;

import com.zjxu.educationapp.common.utils.Result;
import com.zjxu.educationapp.modules.service.CelebrityChatService;
import com.zjxu.educationapp.modules.vo.ChatRequest;
import com.zjxu.educationapp.modules.vo.ChatResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * AI模拟对话相关接口
 */
@RestController
@RequestMapping("/api/celebrityChat")
public class CelebrityChatController {

    @Autowired
    private CelebrityChatService celebrityChatService;

    /**
     * 模拟对话
     * @param request
     * @return
     */
    @PostMapping("/chat")
    public Result<ChatResponse> chatWithCelebrity(@RequestBody ChatRequest request) {
        ChatResponse response = celebrityChatService.chatWithCelebrity(request);
        return Result.ok(response);
    }
}