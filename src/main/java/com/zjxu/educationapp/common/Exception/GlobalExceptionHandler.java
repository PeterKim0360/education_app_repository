package com.zjxu.educationapp.common.Exception;

import cn.dev33.satoken.exception.NotLoginException;
import com.zjxu.educationapp.common.constant.ErrorCode;
import com.zjxu.educationapp.common.utils.Result;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理登录异常
     */
    @ExceptionHandler(NotLoginException.class)
    public Result handleSecurityException(NotLoginException e) {
        if (e.getType().equals(NotLoginException.NOT_TOKEN)) {
            //未提供token
            return Result.error(ErrorCode.NOT_TOKEN_ERROR);
        } else if (e.getType().equals(NotLoginException.INVALID_TOKEN)) {
            //token无效
            return Result.error(ErrorCode.INVALID_TOKEN_ERROR);
        } else if (e.getType().equals(NotLoginException.TOKEN_TIMEOUT)) {
            //token已过期
            return Result.error(ErrorCode.TOKEN_TIMEOUT_ERROR);
        } else {
            //当前用户未登录，未知登录异常
            return Result.error(ErrorCode.UNKNOWN_LOGIN_ERROR);
        }
    }

    @ExceptionHandler
    public Result handleAllException(Exception e){
        e.printStackTrace();
        return Result.error(500,e.getMessage());
    }
}
