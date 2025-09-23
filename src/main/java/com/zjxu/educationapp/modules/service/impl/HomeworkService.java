package com.zjxu.educationapp.modules.service.impl;

import com.zjxu.educationapp.modules.controller.AIHomeworkCorrector;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HomeworkService {
    // 注入配置的API Key和SDK版本
    @Value("${dashscope.api-key}")
    private String dashScopeApiKey;

    @Value("${education.dashscope.sdk-version:2.21.5}")  // 默认为2.21.5
    private String dashScopeSdkVersion;


    /**
     * 处理作业批改请求
     */
    public AIHomeworkCorrector.CorrectionResult correctHomework(
            String systemPrompt,
            String userPrompt,
            List<byte[]> homeworkImageBytes,
            List<String> imageFormats) throws Exception {

        // 直接创建批改器并调用方法（非递归）
        AIHomeworkCorrector corrector = new AIHomeworkCorrector(
                dashScopeApiKey,
                dashScopeSdkVersion
        );

        return corrector.correctHomework(
                systemPrompt,
                userPrompt,
                homeworkImageBytes,
                imageFormats
        );
    }
}
