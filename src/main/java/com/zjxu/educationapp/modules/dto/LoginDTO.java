package com.zjxu.educationapp.modules.dto;

import lombok.Data;

@Data
public class LoginDTO {
    private Long userId;
    private String userName;
    private String phone;
    private String password;
}
