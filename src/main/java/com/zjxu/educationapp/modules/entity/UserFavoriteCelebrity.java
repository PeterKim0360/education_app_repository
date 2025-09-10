package com.zjxu.educationapp.modules.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;

import lombok.Builder;
import lombok.Data;

/**
 * 用户关注的名人
 * @TableName user_favorite_celebrity
 */
@TableName(value ="user_favorite_celebrity")
@Data
@Builder
public class UserFavoriteCelebrity {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 名人ID
     */
    private Long celebrityId;

    /**
     * 创建时间
     */
    private Date createTime;
}