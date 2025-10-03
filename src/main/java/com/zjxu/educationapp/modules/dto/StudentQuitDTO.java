package com.zjxu.educationapp.modules.dto;

import lombok.Data;

@Data
public class StudentQuitDTO {
    private Long teamId;
    private Long userId;
    private Boolean isLeader;
}
