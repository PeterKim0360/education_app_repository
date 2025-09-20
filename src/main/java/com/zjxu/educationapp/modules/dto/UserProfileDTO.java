package com.zjxu.educationapp.modules.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class UserProfileDTO {
    
    @Schema(description = "个人简介")
    private String profile;
}
