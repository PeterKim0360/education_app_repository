package com.zjxu.educationapp.modules.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjxu.educationapp.common.constant.ErrorCode;
import com.zjxu.educationapp.common.utils.Result;
import com.zjxu.educationapp.modules.dto.LoginDTO;
import com.zjxu.educationapp.modules.entity.UserEntity;
import com.zjxu.educationapp.modules.service.UserService;
import com.zjxu.educationapp.modules.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author Kim-Peter
 * @description 针对表【user】的数据库操作Service实现
 * @createDate 2025-08-27 10:33:17
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, UserEntity>
        implements UserService {

    @Override
    public Result login(LoginDTO loginDTO) {
        UserEntity userEntity = getOne(new LambdaQueryWrapper<>(UserEntity.class).eq(UserEntity::getPhone, loginDTO.getPhone()));
        //1.判断用户是否存在
        if (userEntity == null) {
            return Result.error(ErrorCode.PHONE_ERROR);
        }
        //2.判断密码是否正确
        if (!userEntity.getPassword().equals(loginDTO.getPassword())) {
            return Result.error(ErrorCode.PASSWORD_ERROR);
        }
        //3.判断账号是否被冻结
        if (userEntity.getStatus() == 0) {
            return Result.error(ErrorCode.ACCOUNT_STATUS_ERROR);
        }
        //登录成功保存用户id
        StpUtil.login(userEntity.getId());
        log.info("token:{}",StpUtil.getTokenValue());
        return Result.ok(StpUtil.getTokenValue());
    }
}




