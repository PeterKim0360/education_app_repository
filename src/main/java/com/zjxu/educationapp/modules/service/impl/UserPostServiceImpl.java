package com.zjxu.educationapp.modules.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.BooleanUtil;
import com.alibaba.fastjson2.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjxu.educationapp.common.constant.RedisConstant;
import com.zjxu.educationapp.common.utils.Result;
import com.zjxu.educationapp.modules.dto.UserPostCommentDTO;
import com.zjxu.educationapp.modules.dto.UserPostDTO;
import com.zjxu.educationapp.modules.entity.UserEntity;
import com.zjxu.educationapp.modules.entity.UserPostCommentEntity;
import com.zjxu.educationapp.modules.entity.UserPostEntity;
import com.zjxu.educationapp.modules.mapper.UserMapper;
import com.zjxu.educationapp.modules.mapper.UserPostCommentMapper;
import com.zjxu.educationapp.modules.service.UserPostService;
import com.zjxu.educationapp.modules.mapper.UserPostMapper;
import com.zjxu.educationapp.modules.vo.UserPostCommentVO;
import com.zjxu.educationapp.modules.vo.UserPostVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Kim-Peter
 * @description 针对表【user_post】的数据库操作Service实现
 * @createDate 2025-09-05 14:05:35
 */
@Service
public class UserPostServiceImpl extends ServiceImpl<UserPostMapper, UserPostEntity>
        implements UserPostService {
    @Autowired
    private UserPostMapper userPostMapper;
    @Autowired
    private UserPostCommentMapper userPostCommentMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Result<List<UserPostVO>> getPost(Integer page, Integer size) {
        IPage<UserPostEntity> resPage = new Page<>(page, size);
        userPostMapper.selectPage(resPage, new LambdaQueryWrapper<UserPostEntity>().orderByDesc(UserPostEntity::getUpdateTime));
//        等价userPostEntity -> this.getUserPostVO(userPostEntity)
        List<UserPostVO> resList = resPage.getRecords().stream().map(this::getUserPostVO).toList();
        return Result.ok(resList);
    }

    @Override
    public Result<UserPostVO> postDetail(Integer postId) {
        UserPostEntity userPostEntity = getOne(new LambdaQueryWrapper<UserPostEntity>().eq(UserPostEntity::getId, postId));
        if (userPostEntity == null) {
            return Result.error("动态不存在");
        }
        return Result.ok(getUserPostVO(userPostEntity));
    }

    private UserPostVO getUserPostVO(UserPostEntity userPostEntity) {
        UserPostVO userPostVO = new UserPostVO();
        Long commentCount = userPostCommentMapper.selectCount(new LambdaQueryWrapper<UserPostCommentEntity>().eq(UserPostCommentEntity::getPostId, userPostEntity.getId()));
        //忽视重名不同类型属性
        BeanUtil.copyProperties(userPostEntity, userPostVO, "contentImageUrls");
        userPostVO.setCommentCount(commentCount.intValue());

        UserEntity userEntity = userMapper.selectOne(new LambdaQueryWrapper<UserEntity>().eq(UserEntity::getId, userPostEntity.getUserId()));
        userPostVO.setAvatarUrl(userEntity.getAvatarUrl());
        userPostVO.setUserName(userEntity.getUserName());

        String key=RedisConstant.POST_LIKE + userPostEntity.getId();
        userPostVO.setIsLiked(isLiked(key));
        //内容图片可能为空
        if (userPostEntity.getContentImageUrls() != null) {
            userPostVO.setContentImageUrls(JSONArraytoList(userPostEntity.getContentImageUrls().toString(), String.class));
        }
        return userPostVO;
    }

    @Override
    public Result<List<UserPostCommentVO>> postCommentByGet(Integer postId, Integer page, Integer size) {
        IPage<UserPostCommentEntity> resPage = new Page<>(page, size);
        userPostCommentMapper.selectPage(resPage, new LambdaQueryWrapper<UserPostCommentEntity>().eq(UserPostCommentEntity::getPostId, postId));
        List<UserPostCommentVO> list = resPage.getRecords().stream().map(userPostCommentEntity -> {
            UserPostCommentVO userPostCommentVO = BeanUtil.copyProperties(userPostCommentEntity, UserPostCommentVO.class);
            UserEntity userEntity = userMapper.selectOne(new LambdaQueryWrapper<UserEntity>().eq(UserEntity::getId, userPostCommentEntity.getUserId()));
            userPostCommentVO.setCommentUserName(userEntity.getUserName());
            userPostCommentVO.setAvatarUrl(userEntity.getAvatarUrl());

            String key=RedisConstant.COMMENT_LIKE + userPostCommentEntity.getId();
            userPostCommentVO.setIsLiked(isLiked(key));
            return userPostCommentVO;
        }).toList();
        return Result.ok(list);
    }

    @Override
    public Result postByPost(UserPostDTO userPostDTO) {
        UserPostEntity userPostEntity = BeanUtil.copyProperties(userPostDTO, UserPostEntity.class);
        //list必须转成json数组才能存进数据库
        userPostEntity.setContentImageUrls(JSONArray.toJSONString(userPostDTO.getContentImageUrls()));
        userPostEntity.setUserId(StpUtil.getLoginIdAsLong());
        userPostMapper.insert(userPostEntity);
        return Result.ok();
    }

    @Override
    public Result<?> postCommentByPost(UserPostCommentDTO userPostCommentDTO) {
        UserPostCommentEntity userPostCommentEntity = BeanUtil.copyProperties(userPostCommentDTO, UserPostCommentEntity.class);
        userPostCommentEntity.setUserId(StpUtil.getLoginIdAsLong());
        userPostCommentMapper.insert(userPostCommentEntity);
        return Result.ok();
    }

    @Override
    public Result<?> likePost(Integer postId) {
        Long userId = StpUtil.getLoginIdAsLong();
        //1.先查询当前用户是否已经点过赞
        String key = RedisConstant.POST_LIKE + postId;
        if (isLiked(key)) {
            //已点赞，动态点赞数-1
            LambdaUpdateWrapper<UserPostEntity> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(UserPostEntity::getId, postId).setSql("like_count = like_count - 1");
            userPostMapper.update(null, updateWrapper);
            stringRedisTemplate.opsForSet().remove(key, userId.toString());
        } else {
            //未点赞，动态点赞数+1
            LambdaUpdateWrapper<UserPostEntity> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(UserPostEntity::getId, postId).setSql("like_count = like_count + 1");
            userPostMapper.update(null, updateWrapper);
            stringRedisTemplate.opsForSet().add(key, userId.toString());
        }
        return Result.ok();
    }

    @Override
    public Result<?> likeComment(Integer commentId) {
        Long userId = StpUtil.getLoginIdAsLong();
        //1.先查询当前用户是否已经点过赞
        String key = RedisConstant.COMMENT_LIKE + commentId;
        if (isLiked(key)) {
            //已点赞
            LambdaUpdateWrapper<UserPostCommentEntity> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(UserPostCommentEntity::getId, commentId).setSql("like_count = like_count - 1");
            userPostCommentMapper.update(null, updateWrapper);
            stringRedisTemplate.opsForSet().remove(key, userId.toString());
        } else {
            //未点赞
            LambdaUpdateWrapper<UserPostCommentEntity> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(UserPostCommentEntity::getId, commentId).setSql("like_count = like_count + 1");
            userPostCommentMapper.update(null, updateWrapper);
            stringRedisTemplate.opsForSet().add(key, userId.toString());
        }
        return Result.ok();
    }

    private Boolean isLiked(String key) {
        long userId = StpUtil.getLoginIdAsLong();
        return BooleanUtil.isTrue(stringRedisTemplate.opsForSet().isMember(key, Long.toString(userId)));
    }

    private <T> List<T> JSONArraytoList(String json, Class<T> clazz) {
        return JSONArray.parseArray(json, clazz);
    }
}




