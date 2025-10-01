package com.zjxu.educationapp.modules.vo;

import lombok.Data;

@Data
public class PracticeNextVO {
    private Integer nextQuestion;
    private Integer remainingCount;
    private Boolean batchCompleted;
    private Boolean allCompleted;
}
