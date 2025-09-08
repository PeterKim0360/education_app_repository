package com.zjxu.educationapp.modules.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName user_post_comment
 */
@TableName(value ="user_post_comment")
@Data
public class UserPostCommentEntity implements Serializable {
    /**
     * 动态评论id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 评论用户id
     */
    private Long userId;

    /**
     * 动态id
     */
    private Integer postId;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 评论点赞数
     */
    private Integer likeCount;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}