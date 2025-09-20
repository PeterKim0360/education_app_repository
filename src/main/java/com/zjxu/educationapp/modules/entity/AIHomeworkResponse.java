package com.zjxu.educationapp.modules.entity;

import lombok.Data;

import java.util.Date;

/**
 * AI生成作业的响应类
 */
@Data
public class AIHomeworkResponse {
    private String homeworkName;
    private String content;
    private Integer subjectId;
    private Date deadTime;
}
