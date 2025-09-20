package com.zjxu.educationapp.modules.service;

import com.zjxu.educationapp.common.utils.Result;
import com.zjxu.educationapp.modules.dto.LoginDTO;
import com.zjxu.educationapp.modules.dto.UserProfileDTO;
import com.zjxu.educationapp.modules.entity.UserEntity;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zjxu.educationapp.modules.vo.UserInfoVO;

/**
* @author Kim-Peter
* @description 针对表【user】的数据库操作Service
* @createDate 2025-08-27 10:33:17
*/
public interface UserService extends IService<UserEntity> {

    Result login(LoginDTO loginDTO);

    Result<String> updateProfile(UserProfileDTO profileDTO, Long userId);

    Result<UserInfoVO> getUserInfo(Long userId);

    Result register(LoginDTO loginDTO);
}
