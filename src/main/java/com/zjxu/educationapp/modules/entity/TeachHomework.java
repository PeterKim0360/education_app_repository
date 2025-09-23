package com.zjxu.educationapp.modules.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;

import lombok.Builder;
import lombok.Data;

/**
 * 教师作业信息表
 * @TableName teach_homework
 */
@TableName(value ="teach_homework")
@Data
public class TeachHomework {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long homeworkId;

    /**
     * 学科ID
     */
    private Integer subjectId;

    /**
     * 当前用户ID
     */
    private Long userId;

    /**
     * 是否批改：0，未批改；1，已批改
     */
    private Integer correct;

    /**
     * 作业名称
     */
    private String homeworkName;

    /**
     * 截止日期
     */
    private Date deadTime;

    /**
     * 发布日期
     */
    private Date sendTime;

    /**
     * 创建日期
     */
    private Date createdTime;

    /**
     * 作业内容
     */
    private String homeworkContent;

    /**
     * 
     */
    private Date updateTime;

    /**
     * 逻辑删除：0，已删除；1，未删除
     */
    private Integer logicalDeletion;

    /**
     * 图片URL
     */
    private String imageUrls;

}