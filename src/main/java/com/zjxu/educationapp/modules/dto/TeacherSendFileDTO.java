package com.zjxu.educationapp.modules.dto;

import lombok.Data;

@Data
public class TeacherSendFileDTO {
    private Integer subjectId;
    private String fileUrl;
    private String fileDescription;
}
