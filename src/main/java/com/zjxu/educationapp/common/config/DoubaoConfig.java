package com.zjxu.educationapp.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DoubaoConfig {
    @Value("${doubao.api-key:}")
    private String apiKey;

    public String getApiKey() {
        return apiKey;
    }
}

