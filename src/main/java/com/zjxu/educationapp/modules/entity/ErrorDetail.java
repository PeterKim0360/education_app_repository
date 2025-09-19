package com.zjxu.educationapp.modules.entity;

import lombok.Data;

@Data
public class ErrorDetail {
    private String questionType;
    private String questionText;
    private String originalText;
    private String masteryStatus;

    public ErrorDetail(String questionType, String questionText, String originalText, String masteryStatus) {
        this.questionType = questionType;
        this.questionText = questionText;
        this.originalText = originalText;
        this.masteryStatus = masteryStatus;
    }
}
