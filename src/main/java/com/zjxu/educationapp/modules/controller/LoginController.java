package com.zjxu.educationapp.modules.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import com.zjxu.educationapp.common.utils.Result;
import com.zjxu.educationapp.modules.dto.LoginDTO;
import com.zjxu.educationapp.modules.entity.UserEntity;
import com.zjxu.educationapp.modules.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 登录相关接口
 */
@RestController
@Slf4j
@Tag(name="用户登录相关接口")
public class LoginController {
    @Autowired
    private UserService userService;

    /**
     * 登录接口
     * @param loginDTO
     * @return
     */
    @Operation(summary = "登录",description = "传参：phone,password；其它可选")
    @PostMapping("/login")
    public Result login(@RequestBody LoginDTO loginDTO) {
        return userService.login(loginDTO);
    }

    /**
     * 注册接口
     * @param loginDTO
     * @return
     */
    @Operation(summary = "注册",description = "所有参数都要传")
    @PostMapping("/register")
    @Transactional
    public Result register(@Parameter (required = true) @RequestBody LoginDTO loginDTO) {
        UserEntity userEntity = BeanUtil.copyProperties(loginDTO, UserEntity.class);
        userService.save(userEntity);

        return Result.ok();
    }

    /**
     * 登出接口
     * @return
     */
    @Operation(summary = "登出")
    @PostMapping("/logout")
    public Result logout(){
        StpUtil.logout();
        return Result.ok();
    }
}
