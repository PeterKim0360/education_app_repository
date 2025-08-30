package com.zjxu.educationapp.modules.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zjxu.educationapp.common.constant.ErrorCode;
import com.zjxu.educationapp.common.utils.Result;
import com.zjxu.educationapp.modules.dto.LoginDTO;
import com.zjxu.educationapp.modules.entity.UserEntity;
import com.zjxu.educationapp.modules.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 登录相关接口
 */
@RestController
@Slf4j
public class LoginController {
    @Autowired
    private UserService userService;

    /**
     * 登录接口
     * @param loginDTO
     * @return
     */
    @PostMapping("/login")
    public Result login(@RequestBody LoginDTO loginDTO) {
        return userService.login(loginDTO);
    }

    /**
     * 注册接口
     * @param loginDTO
     * @return
     */
    @PostMapping("/register")
    @Transactional
    public Result register(@RequestBody LoginDTO loginDTO) {
        UserEntity userEntity = BeanUtil.copyProperties(loginDTO, UserEntity.class);
        userService.save(userEntity);

        return Result.ok();
    }

    /**
     * 登出接口
     * @return
     */
    @PostMapping("/logout")
    public Result logout(){
        StpUtil.logout();
        return Result.ok();
    }
}
