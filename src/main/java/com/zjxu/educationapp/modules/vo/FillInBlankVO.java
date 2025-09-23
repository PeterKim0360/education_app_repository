package com.zjxu.educationapp.modules.vo;

import lombok.Data;

import java.util.Date;

@Data
public class FillInBlankVO {
    private Integer questionId;
    private Integer subjectId;
    private String questionText;
    private Boolean isMastered;
    private Date createdTime;
    private String correctAnswer;
    private String userAnswer;
}
