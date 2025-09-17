package com.zjxu.educationapp.modules.vo;

import lombok.Data;

import java.util.Date;

@Data
public class TeachSendHWDetailVO {
    private Long homeworkId;
    private String subject;
    private String homeworkName;
    private String homeworkContent;
    private Date sendTime;
    private Date deadTime;
}
