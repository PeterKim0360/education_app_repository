package com.zjxu.educationapp.common.utils;

import com.zjxu.educationapp.common.constant.ErrorCode;
import lombok.Data;

@Data
public class Result<T> {
    private Integer code;
    private String message;
    private T data;

    public static <T> Result<T> ok(){
        return build(200,"成功",null);
    }

    public static <T> Result<T> ok(T data){
        return build(200,"成功",data);
    }

    public static <T> Result<T> error(){
        return build(201,"失败",null);
    }

    public static <T> Result<T> error(ErrorCode errorCode){
        return error(errorCode.getCode(),errorCode.getMessage());
    }
    public static <T> Result<T> error(Integer code,String message){
        return build(code,message,null);
    }

    private static <T> Result<T> build(Integer code,String message,T data){
        Result<T> result = new Result<T>();
        result.setCode(code);
        result.setMessage(message);
        result.setData(data);
        return result;
    }
}

