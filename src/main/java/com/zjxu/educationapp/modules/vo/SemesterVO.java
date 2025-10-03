package com.zjxu.educationapp.modules.vo;

import lombok.Data;

import java.util.Date;

@Data
public class SemesterVO {
    private Long id;
    private String semesterName;
    private Date startDate;
    private Date endDate;
}
