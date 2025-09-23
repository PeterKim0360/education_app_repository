package com.zjxu.educationapp.modules.vo;

import lombok.Data;

import java.util.Date;

@Data
public class SingleChoiceVO {
    private Integer questionId;
    private Integer subjectId;
    private String questionText;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private String correctOption;
    private String userAnswer;
    private Boolean isMastered;
    private Date createdTime;
}
