package com.zjxu.educationapp.modules.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class LoginDTO {
    private String userName;
    private String avatarUrl;
    private String phone;
    private String password;
    @Schema(description = "身份：0-学生，1-老师")
    private Integer identity;
    private String className;
}
