package com.zjxu.educationapp.modules.entity;

import lombok.Data;

import java.util.List;

@Data
public class QuestionResultSummary {
    private String subjectName;
    private String questionType;
    private int questionCount;
    private List<String> suggestions;

    public QuestionResultSummary(String subjectName, String questionType, int questionCount, List<String> suggestions) {
        this.subjectName = subjectName;
        this.questionType = questionType;
        this.questionCount = questionCount;
        this.suggestions = suggestions;
    }
}
