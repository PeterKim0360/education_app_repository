package com.zjxu.educationapp.modules.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CorrectVO {
    private Long homeworkId;
    private Long studentId;
    private String studentName;
    private String teacherComment;
    private BigDecimal score;
}
