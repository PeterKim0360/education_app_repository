package com.zjxu.educationapp.modules.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class UserPostVO{

    /**
     * 用户动态id
     */
    private Integer id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 动态内容（文字）
     */
    private String content;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 评论数
     */
    private Integer commentCount;

    /**
     * 打赏数
     */
    private Integer rewardCount;

    /**
     * 头像url
     */
    private String avatarUrl;

    /**
     * 内容图片url集合
     */
    private List<String> contentImageUrls;

    /**
     * 标题
     */
    private String title;

    /**
     * 当前用户是否点赞
     */
    private Boolean isLiked;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 创建时间
     */
    private Date createTime;


}
