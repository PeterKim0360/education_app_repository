package com.zjxu.educationapp.modules.mapper;

import com.zjxu.educationapp.modules.entity.FriendRelation;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
* @author Kim-Peter
* @description 针对表【friend_relation(好友关系表)】的数据库操作Mapper
* @createDate 2025-09-10 21:23:31
* @Entity com.zjxu.educationapp.modules.entity.FriendRelationEntity
*/
public interface FriendRelationMapper extends BaseMapper<FriendRelation> {

    /**
     * 检查好友关系是否存在
     * @param userId 用户ID
     * @param friendId 好友ID
     * @return 好友关系数量
     */
    int checkFriendRelation(@Param("userId") Long userId, @Param("friendId") Long friendId);

    /**
     * 删除好友关系
     * @param userId 用户ID
     * @param friendId 好友ID
     */
    int deleteFriendRelation(@Param("userId") Long userId, @Param("friendId") Long friendId);}




