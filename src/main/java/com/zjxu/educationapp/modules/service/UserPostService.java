package com.zjxu.educationapp.modules.service;

import com.zjxu.educationapp.common.utils.Result;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zjxu.educationapp.modules.entity.UserPostEntity;

/**
* @author Kim-Peter
* @description 针对表【user_post】的数据库操作Service
* @createDate 2025-09-05 14:05:35
*/
public interface UserPostService extends IService<UserPostEntity> {

    Result post(Integer page, Integer size);

    Result postDetail(Integer postId);

    Result postComment(Integer postId, Integer page, Integer size);
}
