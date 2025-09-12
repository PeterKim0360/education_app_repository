package com.zjxu.educationapp.modules.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class StuHomeWorkCorVO {
    private Long homeworkId;
    //对应学科名称
    private String subject;
    private Integer completeAndCorrect;
    private String homeworkName;
    private Date deadTime;
    private Date sendTime;
    private Date correctTime;
    private BigDecimal score;
}
