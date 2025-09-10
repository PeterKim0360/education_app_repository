package com.zjxu.educationapp.modules.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 名人表
 * @TableName celebrity
 */
@TableName(value ="celebrity")
@Data
public class Celebrity {
    /**
     * 名人ID
     */
    @TableId(type = IdType.AUTO)
    private Long celebrityId;

    /**
     * 名人姓名
     */
    private String celebrityName;

    /**
     * 职业/身份
     */
    private String profession;

    /**
     * 头像
     */
    private String avatarUrl;

    /**
     * 简介
     */
    private String description;

    /**
     * 所处时代
     */
    private String era;

    /**
     * 用于AI角色扮演的提示词
     */
    private String aiPrompt;

    /**
     * 状态：1，启用 0，禁用
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}