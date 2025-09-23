package com.zjxu.educationapp.modules.service.impl;

import com.alibaba.dashscope.aigc.generation.*;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.fastjson.JSON;
import com.zjxu.educationapp.common.config.DashScopeConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.Base64;  // 添加这行导入

import org.springframework.util.StringUtils;
import com.alibaba.dashscope.aigc.generation.Generation;

/**
 * 阿里云百炼API调用服务类，提供通用的AI生成能力
 */
@Service
@Slf4j
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
                .model("qwen3-coder-plus")  // 可根据需要切换模型
                .messages(Arrays.asList(systemMessage, userMessage))  // 消息列表
                .resultFormat(GenerationParam.ResultFormat.MESSAGE)  // 返回格式为MESSAGE
                .maxTokens(1024)
                .temperature(0.7F)
                .build();

        // 调用API并返回结果
        GenerationResult result = generation.call(param);
        return result.getOutput().getChoices().get(0).getMessage().getContent();
    }

    /**
     * 调用AI生成内容（支持直接传递图片数据）
     *
     * @param systemPrompt 系统提示（定义AI角色和行为规范）
     * @param userPrompt   用户提示（具体生成需求）
     * @param imageDatas   图片数据列表
     * @param imageFormats 图片格式列表
     * @return AI生成的文本内容
     * @throws ApiException        API调用异常
     * @throws NoApiKeyException   缺少API Key异常
     * @throws InputRequiredException 输入参数不完整异常
     */
    public String callAIWithImageDatas(String systemPrompt, String userPrompt, List<byte[]> imageDatas, List<String> imageFormats)
            throws ApiException, NoApiKeyException, InputRequiredException {

        // 参数校验
        if (!StringUtils.hasText(systemPrompt) || !StringUtils.hasText(userPrompt)) {
            throw new InputRequiredException("系统提示和用户提示不能为空");
        }
        if (imageDatas == null || imageDatas.isEmpty()) {
            throw new InputRequiredException("图片数据列表不能为空");
        }
        if (imageDatas.size() != imageFormats.size()) {
            throw new InputRequiredException("图片数据和格式列表大小不匹配");
        }

        Generation generation = new Generation();

        // 系统消息
        Message systemMessage = Message.builder()
                .role(Role.SYSTEM.getValue())
                .content(systemPrompt)
                .build();

        // 构建多模态内容（使用新版本支持的格式）
        List<Map<String, String>> contentList = new ArrayList<>();

        // 添加文本内容
        Map<String, String> textContent = new HashMap<>();
        textContent.put("type", "text");
        textContent.put("text", userPrompt);
        contentList.add(textContent);

        // 添加图片内容（备用格式）
        for (int i = 0; i < imageDatas.size(); i++) {
            byte[] imageData = imageDatas.get(i);
            String format = imageFormats.get(i).toLowerCase();

            // 标准化图片格式
            if ("jpg".equals(format)) format = "jpeg";
            if (!Arrays.asList("jpeg", "png", "gif", "webp").contains(format)) {
                format = "jpeg";
            }

            // 生成base64图片数据
            String base64Image = Base64.getEncoder().encodeToString(imageData);

            // 最简格式（直接放在同一层级）
            Map<String, String> imageContent = new HashMap<>();
            imageContent.put("type", "image_url");
            imageContent.put("url", "data:image/" + format + ";base64," + base64Image);
            contentList.add(imageContent);

            log.info("添加图片数据，格式: {}, 大小: {} bytes, base64长度: {}",
                    format, imageData.length, base64Image.length());
        }


        // 构建用户消息
        Message userMessage = Message.builder()
                .role(Role.USER.getValue())
                .content(JSON.toJSONString(contentList))
                .build();

        // 调用参数
        GenerationParam param = GenerationParam.builder()
                .apiKey(apiKey)
                .model("qwen-vl-plus")
                .messages(Arrays.asList(systemMessage, userMessage))
                .resultFormat(GenerationParam.ResultFormat.MESSAGE)
                .temperature(0.7F)
                .topP(0.8)
                .build();

        GenerationResult result = generation.call(param);
        return result.getOutput().getChoices().get(0).getMessage().getContent();
    }


}
