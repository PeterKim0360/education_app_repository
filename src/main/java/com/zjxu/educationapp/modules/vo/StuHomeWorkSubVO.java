package com.zjxu.educationapp.modules.vo;

import lombok.Data;

import java.util.Date;
@Data
public class StuHomeWorkSubVO {
    private Long homeworkId;
    //对应学科名称
    private String subject;
    private Integer completeAndCorrect;
    private String homeworkName;
    private Date deadTime;
    private Date sendTime;
    private Date submitTime;
}
