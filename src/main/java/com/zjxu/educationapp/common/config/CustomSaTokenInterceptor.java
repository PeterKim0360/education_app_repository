package com.zjxu.educationapp.common.config;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.stp.StpUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zjxu.educationapp.common.constant.ErrorCode;
import com.zjxu.educationapp.common.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class CustomSaTokenInterceptor implements HandlerInterceptor {
    //NOTE Sa-Token拦截器自己定义，直接在拦截器中处理异常
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    // 不需要认证的路径
    private final List<String> excludePaths = Arrays.asList(
        "/user/login", "/user/register", "/user/info/",
        "/test/", "/common/", "/callback/stream/",
        "/doc.html", "/webjars/", "/swagger-resources/", "/v2/api-docs/", "/v3/api-docs/",
        "/swagger-ui.html", "/swagger-ui/", "/favicon.ico", "/error", "/actuator/",
        "/single/chat/"
    );

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        // 检查是否是排除路径
        if (isExcludePath(requestURI)) {
            return true;
        }
        
        try {
            //NOTE 此处手动检查登录状态，不使用sa-token自带的拦截器，而是自己配置，防止无法捕获异常而输出堆栈信息（自己实现可以自由控制异常信息）

            // 进行登录校验
            StpUtil.checkLogin();
            return true;
        } catch (NotLoginException e) {
            // 捕获Sa-Token异常，记录简洁日志，直接返回JSON响应
            handleNotLoginException(e, response);
            return false; // 拦截请求
        }
    }
    
    /**
     * 检查是否是排除路径
     */
    private boolean isExcludePath(String requestURI) {
        return excludePaths.stream().anyMatch(requestURI::startsWith);
    }
    
    /**
     * 处理未登录异常
     */
    private void handleNotLoginException(NotLoginException e, HttpServletResponse response) throws IOException {
        ErrorCode errorCode;
        String logMessage;
        
        if (e.getType().equals(NotLoginException.NOT_TOKEN)) {
            errorCode = ErrorCode.NOT_TOKEN_ERROR;
            logMessage = "访问被拦截：未提供token";
        } else if (e.getType().equals(NotLoginException.INVALID_TOKEN)) {
            errorCode = ErrorCode.INVALID_TOKEN_ERROR;
            logMessage = "访问被拦截：token无效";
        } else if (e.getType().equals(NotLoginException.TOKEN_TIMEOUT)) {
            errorCode = ErrorCode.TOKEN_TIMEOUT_ERROR;
            logMessage = "访问被拦截：token已过期";
        } else {
            errorCode = ErrorCode.UNKNOWN_LOGIN_ERROR;
            logMessage = "访问被拦截：未知登录异常";
        }
        
        // 记录简洁的日志，不打印堆栈
        log.error(logMessage);
        
        // 构建响应
        Result<ErrorCode> result = Result.error(errorCode);
        String jsonResponse = objectMapper.writeValueAsString(result);
        
        // 设置响应头
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        
        // 写入响应
        response.getWriter().write(jsonResponse);
        response.getWriter().flush();
    }
} 