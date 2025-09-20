package com.zjxu.educationapp.modules.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zjxu.educationapp.modules.entity.UserPostEntity;
import org.apache.ibatis.annotations.Mapper;

/**
* @author Kim-Peter
* @description 针对表【user_post】的数据库操作Mapper
* @createDate 2025-09-05 14:05:35
* @Entity com.zjxu.educationapp.modules.entity.UserPostEntity
*/
@Mapper
public interface UserPostMapper extends BaseMapper<UserPostEntity> {

}




