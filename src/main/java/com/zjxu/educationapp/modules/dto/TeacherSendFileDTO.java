package com.zjxu.educationapp.modules.dto;

import lombok.Data;

import java.util.List;

@Data
public class TeacherSendFileDTO {
    private Integer subjectId;
    private String fileUrl;
    private String fileDescription;
    private List<Long> classIds;
}
