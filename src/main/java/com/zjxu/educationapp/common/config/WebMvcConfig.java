package com.zjxu.educationapp.common.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //TODO 关于拦截器能不能设置同一个路径拦截post请求，不拦截get请求
        //登录时传给前端token->请求时携带token->判断token是否有效->映射到对应用户id，并全局绑定
        registry.addInterceptor(new SaInterceptor(handler -> {
            SaRouter.match("/**")
                    .notMatch("/login",
                            "/register",
                            "/test/**",
                            "/square/post","/square/post/detail","/square/post/comment")
                    .check(StpUtil::checkLogin);
        })).addPathPatterns("/**");
    }
}
