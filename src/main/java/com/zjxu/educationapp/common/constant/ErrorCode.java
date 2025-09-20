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
    UNSELECTED_FOR_DELETION(408,"未选择删除项"),
    THE_ASSIGNMENT_IS_NOT_OVERDUE(409,"作业未过期"),
    DELETE_FAILED(410,"删除失败"),
    SUBJECT_DO_NOT_EXIST(411,"该学科不存在"),
    THE_JOB_DOES_NOT_EXIST(412,"作业不存在"),
    THE_ASSIGNMENT_IS_OVERDUE(413,"作业已过期"),
    DOES_NOT_EXIST_OR_HAS_NOT_EXPIRED(409,"作业ID不存在或者都未过期"),
    PHONE_EXIST(410,"手机号已存在"),
    STU_NOT_FOUND(411,"学生不存在"),

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
