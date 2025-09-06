package com.zjxu.educationapp.modules.vo;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class UserPostCommentVO {
    /*NOTE 关于表字段设计
    比如content内容字段,userName字段，以后记得加上表前缀，避免不同表字段冲突，不能直接BeanUtil.copyProperties
     */

    /**
     * 评论用户名,手动赋值
     */
    private String commentUserName;

    /**
     * 评论用户头像url,手动赋值
     */
    private String avatarUrl;

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
    private Date createTime;
}
