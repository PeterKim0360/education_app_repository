package com.zjxu.educationapp.modules.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import com.zjxu.educationapp.common.utils.Result;
import com.zjxu.educationapp.modules.dto.LoginDTO;
import com.zjxu.educationapp.modules.dto.UserProfileDTO;
import com.zjxu.educationapp.modules.entity.UserEntity;
import com.zjxu.educationapp.modules.service.UserService;
import com.zjxu.educationapp.modules.vo.UserInfoVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

/**
 * 用户相关接口
 */
@RestController
@Slf4j
@Tag(name="用户相关接口")
@RequestMapping("/user")
public class UserController {
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
        log.info("注册，参数：{}", loginDTO);
        return userService.register(loginDTO);
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

    /**
     * 更新个人简介接口
     * @param profileDTO 个人简介信息
     * @return
     */
    @Operation(summary = "更新个人简介", description = "传参：profile")
    @PostMapping("/updateProfile")
    public Result<String> updateProfile(@RequestBody UserProfileDTO profileDTO) {
        Long userId = StpUtil.getLoginIdAsLong();
        log.info("更新个人简介，用户ID：{}", userId);
        return userService.updateProfile(profileDTO, userId);
    }

    /**
     * 获取用户信息接口
     * @return 用户信息
     */
    @Operation(summary = "获取当前用户信息", description = "获取当前登录用户的所有信息")
    @GetMapping("/info")
    public Result<UserInfoVO> getUserInfo() {
        Long userId = StpUtil.getLoginIdAsLong();
        log.info("获取用户信息，用户ID:{}", userId);
        return userService.getUserInfo(userId);
    }

    @Operation(summary = "根据id获取用户信息", description = "用户id必传")
    @GetMapping("/info/{userId}")
    public Result<UserInfoVO> getUserInfoById(@PathVariable Long userId) {
        log.info("获取用户信息，用户ID:{}", userId);
        return userService.getUserInfo(userId);
    }
}
