package com.zjxu.educationapp.modules.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 分组规则表
 * @TableName grouping_plan
 */
@TableName(value ="grouping_plan")
@Data
public class GroupingPlan {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 随堂id
     */
    private Long workId;

    /**
     * 目标队伍数量
     */
    private Integer targetTeamCount;

    /**
     * 1 允许自由组队，0 反之
     */
    private Integer allowFreeJoin;

    /**
     * 队伍下限数量
     */
    private Integer minTeamSize;

    /**
     * 0-进行中；1-已锁定
     */
    private Integer status;

    /**
     * 创建者id
     */
    private Long createdBy;

    /**
     * 创建时间
     */
    private Date createdTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}