package com.zjxu.educationapp.modules.vo;

import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
public class StuSubjectDetailVO {
    private String subjectName;
    private Integer subjectId;
    private String teacherName;
    private Long teacherId;
    private Map<String, String> file;
}
