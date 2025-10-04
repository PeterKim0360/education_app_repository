package com.zjxu.educationapp.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //使用自定义的Sa-Token拦截器，避免异常堆栈输出
        //登录时传给前端token->请求时携带token->判断token是否有效->映射到对应用户id，并全局绑定
        registry.addInterceptor(new CustomSaTokenInterceptor())
                .addPathPatterns("/**");
    }
}
