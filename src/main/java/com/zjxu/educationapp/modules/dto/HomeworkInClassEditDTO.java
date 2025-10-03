package com.zjxu.educationapp.modules.dto;

import lombok.Data;

import java.util.Date;

@Data
public class HomeworkInClassEditDTO {
    private Long id;
    private Integer subjectId;
    private Long teacherId;
    private String workUrls;
    private String title;
    private Date deadTime;
}
