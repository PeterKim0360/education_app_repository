package com.zjxu.educationapp.modules.dto;

import lombok.Data;

@Data
public class LoginDTO {
    private String userName;
    private String avatarUrl;
    private String phone;
    private String password;
}
