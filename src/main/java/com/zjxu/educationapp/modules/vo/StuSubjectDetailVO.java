package com.zjxu.educationapp.modules.vo;

import lombok.Data;

import java.util.Date;

@Data
public class StuSubjectDetailVO {
    private String subjectName;
    private Integer subjectId;
    private String teacherName;
    private Long teacherId;
    private Date uploadTime;
    private String fileUrl;
    private String fileDescription;
}
