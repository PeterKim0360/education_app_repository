package com.zjxu.educationapp.common.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
public class DashScopeConfig {
    @Value("${dashscope.api-key}")
    private String apiKey;
    @Value("${education.alioss.access-key-id}")
    private String accessKeyId;

    @Value("${education.alioss.access-key-secret}")
    private String accessKeySecret;
    public String getApiKey() {
        return apiKey;
    }
    public String getAccessKeyId() {
        return accessKeyId;
    }
    public String getAccessKeySecret() {
        return accessKeySecret;
    }
}
