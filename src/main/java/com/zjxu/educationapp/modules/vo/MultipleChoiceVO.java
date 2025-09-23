package com.zjxu.educationapp.modules.vo;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class MultipleChoiceVO {
    private Integer questionId;
    private Integer subjectId;
    private String questionText;
    private List<String> options;
    private String correctOption;
    private String userAnswer;
    private Boolean isMastered;
    private Date createdTime;
}
