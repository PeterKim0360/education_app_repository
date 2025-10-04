package com.zjxu.educationapp.modules.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 小组表
 * @TableName group_team
 */
@TableName(value ="group_team")
@Data
public class GroupTeam {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 练习id
     */
    private Long workId;

    /**
     * 课堂id
     */
    private Integer subjectId;

    /**
     * 小组名
     */
    private String name;

    /**
     * 理想人数
     */
    private Integer capacity;

    /**
     * 队长id
     */
    private Long leaderId;

    /**
     * 0-进行中 1-锁定(禁止再加人)
     */
    private Integer status;

    /**
     * 创建者（老师id）
     */
    private Long createdBy;

    /**
     * 创建时间
     */
    private Date createdTime;

    /**
     * 更新时间
     */
    private Date updatedTime;

    /**
     * 当前小组人数
     */
    private Integer currentNum;

    /**
     * 逻辑删除 0-未删除 1-已删除
     */
    private Integer logicalDel;
}