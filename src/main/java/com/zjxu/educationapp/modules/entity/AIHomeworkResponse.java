package com.zjxu.educationapp.modules.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * AI生成作业的响应类
 */
@Data
public class AIHomeworkResponse {
    // getter和setter方法
    private String homeworkName;
    private String content;
    // 确保有默认构造函数
    public AIHomeworkResponse() {}

}