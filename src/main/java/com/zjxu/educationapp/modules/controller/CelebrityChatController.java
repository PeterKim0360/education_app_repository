package com.zjxu.educationapp.modules.controller;

import com.zjxu.educationapp.common.utils.Result;
import com.zjxu.educationapp.modules.service.CelebrityChatService;
import com.zjxu.educationapp.modules.vo.ChatRequest;
import com.zjxu.educationapp.modules.vo.ChatResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * AI模拟对话相关接口
 */
@RestController
@Tag(name = "AI模拟对话相关接口")
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

    /**
     * 初始化对话，让AI发送欢迎消息
     */
    @GetMapping("/init")
    public Result<ChatResponse> initChatByAI(@RequestParam("celebrityId") Long celebrityId){
        ChatResponse chatResponse=celebrityChatService.initByAI(celebrityId);
        return Result.ok(chatResponse);
    }
}