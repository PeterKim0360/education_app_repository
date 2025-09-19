package com.zjxu.educationapp.modules.vo;

import lombok.Data;

import java.util.Date;

@Data
public class TeachSendHWSimpleVO {
    private Long homeworkId;
    private String subject;
    private String homeworkName;
    private Date sendTime;
    private Date deadTime;
}
