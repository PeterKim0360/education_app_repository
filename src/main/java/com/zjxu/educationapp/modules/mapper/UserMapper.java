package com.zjxu.educationapp.modules.mapper;

import com.zjxu.educationapp.modules.entity.UserEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author Kim-Peter
* @description 针对表【user】的数据库操作Mapper
* @createDate 2025-08-27 10:33:17
* @Entity com.zjxu.educationapp.modules.entity.UserEntity
*/
@Mapper
public interface UserMapper extends BaseMapper<UserEntity> {

}




