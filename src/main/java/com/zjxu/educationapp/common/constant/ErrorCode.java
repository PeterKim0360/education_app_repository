package com.zjxu.educationapp.common.constant;

import lombok.Getter;

@Getter
public enum ErrorCode {

    NOT_TOKEN_ERROR(402,"未提供token"),
    INVALID_TOKEN_ERROR(403,"token无效"),
    TOKEN_TIMEOUT_ERROR(404,"token已过期"),
    UNKNOWN_LOGIN_ERROR(405,"当前用户未登录，未知登录异常"),
    UPLOAD_FAILED(406,"文件上传失败"),
    FOLLOWED(407,"已关注过该名人"),

    PASSWORD_ERROR(401,"密码错误"),
    PHONE_ERROR(401,"手机号不存在"),
    ACCOUNT_STATUS_ERROR(401,"账号被冻结");
    private int code;
    private String message;

    private ErrorCode(int code, String message){
        this.code=code;
        this.message=message;
    }
}
