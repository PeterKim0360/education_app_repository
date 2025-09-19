package com.zjxu.educationapp.modules.vo;

import lombok.Data;

import java.util.Date;

@Data
public class TeachCreateHWSimpleVO {
    private Long homeworkId;
    private String subject;
    private String homeworkName;
    private Date deadTime;
    private Date createdTime;
    private Date updateTime;
}
