package com.zjxu.educationapp.modules.vo;

import lombok.Data;

import java.util.List;
@Data
public class StudentSubjectsVO {
    private Integer subjectId;
    private String subjectName;
    private String teacherName;
    private String avatarUrl;
    private Integer status;
}
