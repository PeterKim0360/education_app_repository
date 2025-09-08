package com.zjxu.educationapp.modules.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName user_post
 */
@TableName(value ="user_post")
@Data
public class UserPostEntity implements Serializable {
    /**
     * 用户动态id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 用户id
     */
    private Long userId;


    /**
     * 动态内容（文字）
     */
    private String content;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 打赏数
     */
    private Integer rewardCount;

    /**
     * 内容图片url集合
     */
    private Object contentImageUrls;

    /**
     * 标题
     */
    private String title;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;



    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}