package com.zjxu.educationapp.modules.vo;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class SelectCourseVO {
    private Integer subjectId;
    private Map<String,Long> classInfo;
}
