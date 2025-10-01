package com.zjxu.educationapp.modules.dto;

import lombok.Data;

@Data
public class StudentDTO {
    private Integer id;
    private String name;
    private String avatarUrl;  // 头像URL，可为null
    private Boolean isLeader = false;  // 是否为组长
}