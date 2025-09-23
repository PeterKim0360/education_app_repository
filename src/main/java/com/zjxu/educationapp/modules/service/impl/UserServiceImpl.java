package com.zjxu.educationapp.modules.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjxu.educationapp.common.constant.ErrorCode;
import com.zjxu.educationapp.common.utils.Result;
import com.zjxu.educationapp.modules.dto.LoginDTO;
import com.zjxu.educationapp.modules.dto.UserProfileDTO;
import com.zjxu.educationapp.modules.entity.ClassEntity;
import com.zjxu.educationapp.modules.entity.StudentClass;
import com.zjxu.educationapp.modules.entity.UserEntity;
import com.zjxu.educationapp.modules.mapper.ClassMapper;
import com.zjxu.educationapp.modules.mapper.StudentClassMapper;
import com.zjxu.educationapp.modules.service.UserService;
import com.zjxu.educationapp.modules.mapper.UserMapper;
import com.zjxu.educationapp.modules.vo.UserInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Kim-Peter
 * @description 针对表【user】的数据库操作Service实现
 * @createDate 2025-08-27 10:33:17
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, UserEntity>
        implements UserService {
    @Autowired
    private StudentClassMapper studentClassMapper;
    @Autowired
    private ClassMapper classMapper;
    @Override
    public Result login(@RequestBody LoginDTO loginDTO) {
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

        Map<String, Object> result = new HashMap<>(2);
        result.put("satoken", StpUtil.getTokenValue());
        result.put("userId", userEntity.getId());
        log.info("satoken:{}", StpUtil.getTokenValue());
        return Result.ok(result);
    }

    @Override
    public Result<String> updateProfile(UserProfileDTO profileDTO, Long userId) {
        // 检查用户是否存在
        UserEntity user = getById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }

        // 更新个人简介
        user.setProfile(profileDTO.getProfile());
        boolean success = updateById(user);

        if (success) {
            return Result.ok("个人简介更新成功");
        } else {
            return Result.error("个人简介更新失败");
        }
    }

    @Override
    public Result<UserInfoVO> getUserInfo(Long userId) {
        // 检查用户是否存在
        UserEntity user = getById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }

        // 将UserEntity转换为UserInfoVO
        UserInfoVO userInfoVO = new UserInfoVO();
        BeanUtil.copyProperties(user, userInfoVO);
        if(user.getIdentity() == 0){
            //学生独有信息
            StudentClass studentClass = studentClassMapper.selectOne(new LambdaQueryWrapper<StudentClass>().eq(StudentClass::getStudentId, userId));
            ClassEntity classEntity = classMapper.selectOne(new LambdaQueryWrapper<ClassEntity>().eq(ClassEntity::getClassId, studentClass.getClassId()));
            userInfoVO.setClassName(classEntity.getClassName());
        }
        return Result.ok(userInfoVO);
    }

    @Override
    public Result register(LoginDTO loginDTO) {
        UserEntity judgeExist = getOne(new LambdaQueryWrapper<>(UserEntity.class).eq(UserEntity::getPhone, loginDTO.getPhone()));
        if(judgeExist != null){
            return Result.error(ErrorCode.PHONE_EXIST);
        }
        UserEntity userEntity = BeanUtil.copyProperties(loginDTO, UserEntity.class);
        this.save(userEntity);
        Long userId = userEntity.getId();
        ClassEntity classEntity = new ClassEntity();
        classEntity.setClassName(loginDTO.getClassName());
        classMapper.insert(classEntity);

        Long classId = classEntity.getClassId();
        StudentClass studentClass = new StudentClass();
        studentClass.setStudentId(userId);
        studentClass.setClassId(classId);
        studentClassMapper.insert(studentClass);
        return Result.ok("注册成功");
    }
}




