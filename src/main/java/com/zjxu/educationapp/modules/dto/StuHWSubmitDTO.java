package com.zjxu.educationapp.modules.dto;

import lombok.Data;

@Data
public class StuHWSubmitDTO {
    private Long homeworkId;
    private Integer subjectId;
    private String content;
}
