package com.zjxu.educationapp.modules.vo;

import lombok.Data;

import java.util.Date;

@Data
public class CourseHistoryVO {
    private String teacherName;
    private String subjectName;
    private String fileUrl;
    private String fileDescription;
    private Date uploadTime;
}
