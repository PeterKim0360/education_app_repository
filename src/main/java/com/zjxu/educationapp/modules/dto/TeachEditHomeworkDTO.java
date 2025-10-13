package com.zjxu.educationapp.modules.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;
@Data
public class TeachEditHomeworkDTO {
    private Long homeworkId;
    private Integer subjectId;
    private String homeworkName;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date deadTime;
    // 富文本内容（含图片链接）
    private String homeworkContent;
    // 专门用于存储图片URL列表
    private List<String> imageUrls;
}
