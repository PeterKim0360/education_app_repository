package com.zjxu.educationapp.modules.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.zjxu.educationapp.common.utils.Result;
import lombok.Data;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

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

    @GetMapping("/test/compute")
    public Result<ComputeDTO> compute(@RequestParam Integer type,@RequestParam Integer a,@RequestParam Integer b){
        /*
        加减乘除对应type=1 2 3 4
         */
        if(type<=0||type>4){
            return Result.error();
        }
        ComputeDTO computeDTO = new ComputeDTO();
        computeDTO.setUserId(114514);
        computeDTO.setUserName("张三");
        computeDTO.setCurrentTime(new Date());
        switch (type){
            case 1:
                computeDTO.setAns(a+b);
                return Result.ok(computeDTO);
            case 2:
                computeDTO.setAns(a-b);
                return Result.ok(computeDTO);
            case 3:
                computeDTO.setAns(a*b);
                return Result.ok(computeDTO);
            case 4:
                computeDTO.setAns(a/b);
                return Result.ok(computeDTO);
            default:
                return Result.error();
        }
    }
    //NOTE 关于静态内部类和非静态内部类
    @Data
    private static class ComputeDTO{
        private Integer ans;
        private String userName;
        private Integer userId;
        private Date currentTime;
    }
}
