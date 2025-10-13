package com.zjxu.educationapp.modules.vo;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class TeachCreateHWDetailVO {
    private Long homeworkId;
    private String subject;
    private Integer subjectId;
    private String homeworkName;
    private String homeworkContent;
    private Date deadTime;
    private Date createdTime;
    private Date updateTime;
    private List<String> imageUrls;
}
