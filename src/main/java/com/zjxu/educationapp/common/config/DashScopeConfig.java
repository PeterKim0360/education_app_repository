package com.zjxu.educationapp.common.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
public class DashScopeConfig {
    @Value("${dashscope.api-key}")
    private String apiKey;
    public String getApiKey() {
        return apiKey;
    }
}
