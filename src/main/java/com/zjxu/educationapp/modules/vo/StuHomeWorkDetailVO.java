package com.zjxu.educationapp.modules.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class StuHomeWorkDetailVO {
    private Long homeworkId;
    private String subject;
    private String homeworkName;
    private String homeworkContent;
    private Date deadTime;
    // 1-未完成，2-已提交未批改，3-已批改
    private Integer completeAndCorrect;
    private List<String> imageUrls;
    // 提交内容（仅 status >= 2 时有）
    private String studentContent;
    //提交时间（仅 status >= 2 时有）
    private Date submitTime;
    // 分数（仅 status == 3 时有）
    private BigDecimal score;
    // 教师评语（仅 status == 3 时有）
    private String teacherComment;
    //批改时间（仅 status == 3 时有）
    private Date correctTime;
}
