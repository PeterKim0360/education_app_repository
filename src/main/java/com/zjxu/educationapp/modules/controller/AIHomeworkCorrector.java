package com.zjxu.educationapp.modules.controller;

import com.alibaba.dashscope.aigc.generation.*;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * AI作业批改器，兼容不同版本的DashScope SDK
 */
public class AIHomeworkCorrector {
    private static final Logger log = LoggerFactory.getLogger(AIHomeworkCorrector.class);
    private final String apiKey;
    private final String sdkVersion;
    private final Generation generation;

    // 批改结果封装类
    public static class CorrectionResult {
        private final int score;
        private final String comment;

        public CorrectionResult(int score, String comment) {
            this.score = score;
            this.comment = comment;
        }

        public double getScore() { return score; }
        public String getComment() { return comment; }
    }

    // 构造器初始化
    public AIHomeworkCorrector(String apiKey, String sdkVersion) {
        this.apiKey = apiKey;
        this.sdkVersion = Optional.ofNullable(sdkVersion).orElse("2.21.5");
        this.generation = new Generation();
    }

    /**
     * 核心批改方法
     */
    public CorrectionResult correctHomework(
            String systemPrompt,
            String userPrompt,
            List<byte[]> imageDatas,
            List<String> imageFormats) throws Exception {

        // 参数校验
        validateParameters(systemPrompt, userPrompt, imageDatas, imageFormats);

        // 根据SDK版本选择调用方式
        String aiResponse;
        aiResponse = callOldVersionApi(systemPrompt, userPrompt, imageDatas, imageFormats);

        // 解析AI返回结果
        return parseAIResponse(aiResponse);
    }

    /**
     * 核心批改方法（支持直接传递图片URL）
     */
    public CorrectionResult correctHomeworkWithImageUrls(
            String systemPrompt,
            String userPrompt,
            List<String> imageUrls) throws Exception {

        // 参数校验
        if (!StringUtils.hasText(systemPrompt) || !StringUtils.hasText(userPrompt)) {
            throw new InputRequiredException("系统提示和用户提示不能为空");
        }
        if (imageUrls == null || imageUrls.isEmpty()) {
            throw new InputRequiredException("图片URL列表不能为空");
        }

        // 调用API
        String aiResponse = callOldVersionApiWithImageUrl(systemPrompt, userPrompt, imageUrls);

        // 解析AI返回结果
        return parseAIResponse(aiResponse);
    }

    /**
     * 校验输入参数
     */
    private void validateParameters(
            String systemPrompt,
            String userPrompt,
            List<byte[]> imageDatas,
            List<String> imageFormats) throws InputRequiredException {

        if (!StringUtils.hasText(systemPrompt) || !StringUtils.hasText(userPrompt)) {
            throw new InputRequiredException("系统提示和用户提示不能为空");
        }
        if (imageDatas == null || imageDatas.isEmpty()) {
            throw new InputRequiredException("图片数据列表不能为空");
        }
        if (imageFormats == null || imageFormats.size() != imageDatas.size()) {
            throw new InputRequiredException("图片格式列表不完整或与图片数量不匹配");
        }
    }

    /**
     * 调用旧版本SDK（使用JSON字符串）
     */
    private String callOldVersionApi(
            String systemPrompt,
            String userPrompt,
            List<byte[]> imageDatas,
            List<String> imageFormats) throws ApiException, NoApiKeyException, InputRequiredException {

        // 构建系统消息
        Message systemMessage = Message.builder()
                .role(Role.SYSTEM.getValue())
                .content(systemPrompt)
                .build();

        // 构建多模态内容
        List<Map<String, Object>> contentList = new ArrayList<>();

        // 添加文本内容
        Map<String, Object> textContent = new HashMap<>();
        textContent.put("type", "text");
        textContent.put("text", userPrompt);
        contentList.add(textContent);

        // 添加图片内容（使用DashScope推荐的格式）
        for (int i = 0; i < imageDatas.size(); i++) {
            byte[] imageData = imageDatas.get(i);
            String format = normalizeImageFormat(imageFormats.get(i));

            // 将图片转换为base64编码
            String base64Image = Base64.getEncoder().encodeToString(imageData);

            // 构建图片内容（使用DashScope官方推荐的格式）
            Map<String, Object> imageContent = new HashMap<>();
            imageContent.put("type", "image");
            imageContent.put("image", "data:image/" + format + ";base64," + base64Image);
            contentList.add(imageContent);
        }

        // 构建用户消息
        Message userMessage = Message.builder()
                .role(Role.USER.getValue())
                .content(JSON.toJSONString(contentList))
                .build();

        // 调用API
        GenerationParam param = GenerationParam.builder()
                .apiKey(apiKey)
                .model("qwen-vl-plus")
                .messages(Arrays.asList(systemMessage, userMessage))
                .resultFormat(GenerationParam.ResultFormat.MESSAGE)
                .build();

        GenerationResult result = generation.call(param);
        return result.getOutput().getChoices().get(0).getMessage().getContent();
    }

    /**
     * 调用旧版本SDK（使用JSON字符串）- 直接传递图片URL版本
     */
    private String callOldVersionApiWithImageUrl(
            String systemPrompt,
            String userPrompt,
            List<String> imageUrls) throws ApiException, NoApiKeyException, InputRequiredException {

        // 构建系统消息
        Message systemMessage = Message.builder()
                .role(Role.SYSTEM.getValue())
                .content(systemPrompt)
                .build();

        // 构建多模态内容
        List<Map<String, Object>> contentList = new ArrayList<>();

        // 添加文本内容
        Map<String, Object> textContent = new HashMap<>();
        textContent.put("type", "text");
        textContent.put("text", userPrompt);
        contentList.add(textContent);

        // 添加图片内容（使用DashScope支持的直接URL格式：image_url）
        for (String imageUrl : imageUrls) {
            Map<String, Object> imageContent = new HashMap<>();
            imageContent.put("type", "image");
            imageContent.put("image_url", imageUrl);  // 直接传递图片URL
            contentList.add(imageContent);
        }

        // 构建用户消息
        Message userMessage = Message.builder()
                .role(Role.USER.getValue())
                .content(JSON.toJSONString(contentList))
                .build();

        // 调用API
        GenerationParam param = GenerationParam.builder()
                .apiKey(apiKey)
                .model("qwen-vl-plus")
                .messages(Arrays.asList(systemMessage, userMessage))
                .resultFormat(GenerationParam.ResultFormat.MESSAGE)
                .build();

        GenerationResult result = generation.call(param);
        return result.getOutput().getChoices().get(0).getMessage().getContent();
    }
    /**
     * 创建dataUrl（用于新版本）
     */
    private String createDataUrl(byte[] imageData, String format) {
        String normalizedFormat = normalizeImageFormat(format);
        String base64Image = Base64.getEncoder().encodeToString(imageData);
        return "data:image/" + normalizedFormat + ";base64," + base64Image;
    }

    /**
     * 标准化图片格式
     */
    private String normalizeImageFormat(String format) {
        if (format == null) return "jpeg";
        String lowerFormat = format.toLowerCase();
        return "jpg".equals(lowerFormat) ? "jpeg" :
                Arrays.asList("png", "gif", "webp").contains(lowerFormat) ? lowerFormat : "jpeg";
    }

    /**
     * 解析AI返回的JSON结果
     */
    private CorrectionResult parseAIResponse(String aiResponse) {
        try {
            JSONObject json = JSON.parseObject(aiResponse);
            int score = json.getInteger("score");
            String comment = json.getString("comment");

            // 校验评分范围
            if (score < 0 || score > 100) {
                throw new IllegalArgumentException("评分必须在0-100之间");
            }

            return new CorrectionResult(score, Optional.ofNullable(comment).orElse("未提供评语"));
        } catch (Exception e) {
            log.error("解析AI响应失败: {}", aiResponse, e);
            throw new RuntimeException("解析批改结果失败，请重试");
        }
    }
}
