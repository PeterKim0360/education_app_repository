package com.zjxu.educationapp.modules.vo;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class HomeworkSubmissionVO {
    private Long homeworkId;
    private Long studentId;
    private String studentName;
    private List<String> submitContent;
    private Date submitTime;
    //批改后
    private Integer score;
    private String comment;
}