package com.zjxu.educationapp.modules.vo;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class TrueFalseVO {
    private Integer questionId;
    private Integer subjectId;
    private String questionText;
    private List<String> options;
    private Boolean  isMastered;
    private Date createdTime;
    private String correctResult;
    private String TrueFalseUserAnswer;
}
