package com.zjxu.educationapp.modules.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class HomeworkSubmissionDTO {
    private Long homeworkId;
    private Long studentId;
    private String teacherComment;
    private BigDecimal score;

}
