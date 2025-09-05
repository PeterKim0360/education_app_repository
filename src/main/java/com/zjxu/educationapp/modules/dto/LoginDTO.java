package com.zjxu.educationapp.modules.dto;

import lombok.Data;

/**
 * 登录请求
 */
@Data
public class LoginDTO {
    /**
     * 手机号
     */
    private String phone;
    /**
     * 密码
     */
    private String password;
    /**
     * 用户名
     */
    private String userName;
}
