package com.zjxu.educationapp.modules.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class TeachCreateHomeworkDTO {
    private Long homeworkId;
    private Integer subjectId;
    private String homeworkName;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date deadTime;
    // 富文本内容（含图片链接）
    private String homeworkContent;
}
