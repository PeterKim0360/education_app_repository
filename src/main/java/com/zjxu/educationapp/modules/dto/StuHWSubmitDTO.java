package com.zjxu.educationapp.modules.dto;

import lombok.Data;

import java.util.List;

@Data
public class StuHWSubmitDTO {
    private Long homeworkId;
    private Integer subjectId;
    private List<String> studentContent;
}
