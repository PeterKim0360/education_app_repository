package com.zjxu.educationapp.modules.vo;

import lombok.Data;

import java.util.List;

@Data
public class PracticeStateVO {
    private Long sessionId;
    private Integer subjectId;
    private Integer currentBatch;
    private Integer totalBatches;
    private Boolean completed;
    private Integer nextQuestion;
    private Integer remainingCount;
    private List<ErrorQuestionsVO> pendingQueue;
}


