package com.zjxu.educationapp.modules.service.impl;

import java.net.URLDecoder;
import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.fastjson.JSON;
import com.zjxu.educationapp.common.config.DashScopeConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

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

    /**
     * 调用AI生成内容（支持复杂图片输入场景）
     * @param systemPrompt 系统提示（定义AI角色和行为规范）
     * @param userPrompt 用户提示（具体生成需求）
     * @param imageUrls 图片URL列表
     * @return AI生成的文本内容
     * @throws ApiException API调用异常
     * @throws NoApiKeyException 缺少API Key异常
     * @throws InputRequiredException 输入参数不完整异常
     */
    public String callAIWithImages(String systemPrompt, String userPrompt, List<String> imageUrls)
            throws ApiException, NoApiKeyException, InputRequiredException {

        // 创建生成器实例
        Generation generation = new Generation();

        // 构建系统消息（定义AI角色）
        Message systemMessage = Message.builder()
                .role(Role.SYSTEM.getValue())
                .content(systemPrompt)
                .build();

        // 构建用户消息内容
        List<Map<String, Object>> contentList = new ArrayList<>();

        // 添加文本内容
        Map<String, Object> textContent = new HashMap<>();
        textContent.put("type", "text");
        textContent.put("text", userPrompt);
        contentList.add(textContent);

        // 添加图片内容（处理URL编码）
        if (imageUrls != null && !imageUrls.isEmpty()) {
            for (String imageUrl : imageUrls) {
                // 标准化图片URL
                String standardizedUrl = standardizeImageUrl(imageUrl);

                // 添加调试日志
                log.info("处理图片URL - 原始: {}, 标准化: {}", imageUrl, standardizedUrl);

                // 可选：验证URL可访问性
                // if (!isImageUrlAccessible(standardizedUrl)) {
                //     log.warn("图片URL不可访问: {}", standardizedUrl);
                // }

                Map<String, Object> imageContent = new HashMap<>();
                imageContent.put("type", "image_url");
                Map<String, String> imageUrlMap = new HashMap<>();
                imageUrlMap.put("url", standardizedUrl);
                imageContent.put("image_url", imageUrlMap);
                contentList.add(imageContent);
            }
        }

        // 构建用户消息
        Message userMessage = Message.builder()
                .role(Role.USER.getValue())
                .content(JSON.toJSONString(contentList))
                .build();

        // 构建调用参数（使用支持视觉的模型）
        GenerationParam param = GenerationParam.builder()
                .apiKey(apiKey)
                .model("qwen-vl-plus")  // 使用支持视觉的模型
                .messages(Arrays.asList(systemMessage, userMessage))
                .resultFormat(GenerationParam.ResultFormat.MESSAGE)
                .build();

        // 调用API并返回结果
        GenerationResult result = generation.call(param);
        return result.getOutput().getChoices().get(0).getMessage().getContent();
    }

    /**
     * 标准化图片URL（处理编码问题）
     * @param url 原始URL
     * @return 标准化的URL
     */
    private String standardizeImageUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            return null;
        }

        try {
            // 保存原始URL用于日志
            String originalUrl = url;

            // 多次解码直到URL不再变化（处理可能的多重编码）
            String decodedUrl = url;
            String previousUrl;
            int maxDecodeAttempts = 5; // 最大解码尝试次数
            int decodeAttempts = 0;

            do {
                previousUrl = decodedUrl;
                try {
                    decodedUrl = java.net.URLDecoder.decode(previousUrl, StandardCharsets.UTF_8.name());
                    decodeAttempts++;
                } catch (Exception e) {
                    // 解码失败，跳出循环
                    break;
                }
            } while (!decodedUrl.equals(previousUrl) && decodeAttempts < maxDecodeAttempts);

            log.debug("URL标准化: {} -> {}", originalUrl, decodedUrl);
            return decodedUrl;

        } catch (Exception e) {
            // 如果处理失败，记录日志并返回原始URL
            log.warn("URL标准化失败，使用原始URL: {}", url, e);
            return url;
        }
    }
    /**
     * 验证图片URL是否可访问
     * @param imageUrl 图片URL
     * @return 是否可访问
     */
    public boolean isImageUrlAccessible(String imageUrl) {
        try {
            java.net.URL url = new java.net.URL(imageUrl);
            java.net.HttpURLConnection connection = (java.net.HttpURLConnection) url.openConnection();
            connection.setRequestMethod("HEAD");
            connection.setConnectTimeout(5000); // 5秒连接超时
            connection.setReadTimeout(5000);    // 5秒读取超时
            int responseCode = connection.getResponseCode();
            boolean accessible = responseCode == 200;
            log.debug("图片URL {} 可访问性: {}", imageUrl, accessible);
            return accessible;
        } catch (Exception e) {
            log.warn("检查图片URL可访问性失败: {}", imageUrl, e);
            return false;
        }
    }


}
