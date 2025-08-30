package com.zjxu.educationapp.modules.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.zjxu.educationapp.common.utils.Result;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试
 */
@RestController
public class TestController {
    @GetMapping("/test/{id}")
    public Result Test(@PathVariable Integer id){
        System.out.println("通过拦截器校验");
        //指定用户id进行判断用户是否登录（原理：通过请求头中的sa-token映射到用户id进行判断，并全局绑定用户id）
        System.out.println(StpUtil.isLogin(id));
        //登录时传给前端token->请求时携带token->判断token是否有效->映射到对应用户id，并全局绑定
        StpUtil.checkLogin();
        return Result.ok(StpUtil.getTokenValue());
    }
}
