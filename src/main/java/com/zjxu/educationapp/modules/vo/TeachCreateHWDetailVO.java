package com.zjxu.educationapp.modules.vo;

import lombok.Data;

import java.util.Date;

@Data
public class TeachCreateHWDetailVO {
    private Long homeworkId;
    private String subject;
    private String homeworkName;
    private String homeworkContent;
    private Date createdTime;
    private Date updateTime;
}
