package com.zjxu.educationapp.modules.service.impl;

import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.zjxu.educationapp.common.config.DashScopeConfig;
import org.springframework.stereotype.Service;
import java.util.Arrays;

/**
 * 阿里云百炼API调用服务类，提供通用的AI生成能力
 */
@Service
public class AIGCService {

    private final String apiKey;

    /**
     * 通过配置类注入API Key
     * @param dashScopeConfig 百炼API配置类（存储API Key）
     */
    public AIGCService(DashScopeConfig dashScopeConfig) {
        this.apiKey = dashScopeConfig.getApiKey();
    }

    /**
     * 调用AI生成内容（支持自定义系统提示和用户提示）
     * @param systemPrompt 系统提示（定义AI角色和行为规范）
     * @param userPrompt 用户提示（具体生成需求）
     * @return AI生成的文本内容
     * @throws ApiException API调用异常
     * @throws NoApiKeyException 缺少API Key异常
     * @throws InputRequiredException 输入参数不完整异常
     */
    public String callAI(String systemPrompt, String userPrompt)
            throws ApiException, NoApiKeyException, InputRequiredException {
        // 创建生成器实例
        Generation generation = new Generation();

        // 构建系统消息（定义AI角色）
        Message systemMessage = Message.builder()
                .role(Role.SYSTEM.getValue())
                .content(systemPrompt)
                .build();

        // 构建用户消息（具体需求）
        Message userMessage = Message.builder()
                .role(Role.USER.getValue())
                .content(userPrompt)
                .build();

        // 构建调用参数
        GenerationParam param = GenerationParam.builder()
                .apiKey(apiKey)  // 从配置获取API Key
                .model("qwen-plus")  // 可根据需要切换模型（如"qwen-turbo"）
                .messages(Arrays.asList(systemMessage, userMessage))  // 消息列表
                .resultFormat(GenerationParam.ResultFormat.MESSAGE)  // 返回格式为MESSAGE
                .build();

        // 调用API并返回结果
        GenerationResult result = generation.call(param);
        return result.getOutput().getChoices().get(0).getMessage().getContent();
    }

    /**
     * 简化调用方法（使用默认系统提示）
     * @param userPrompt 用户提示（具体生成需求）
     * @return AI生成的文本内容
     * @throws ApiException 同上
     * @throws NoApiKeyException 同上
     * @throws InputRequiredException 同上
     */
    public String callAI(String userPrompt)
            throws ApiException, NoApiKeyException, InputRequiredException {
        // 默认系统提示：通用助手角色
        String defaultSystemPrompt = "你是一个专业的内容生成助手，能严格按照用户要求的格式生成内容，内容准确、简洁。";
        return callAI(defaultSystemPrompt, userPrompt);
    }
}
