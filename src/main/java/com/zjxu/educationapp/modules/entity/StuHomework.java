package com.zjxu.educationapp.modules.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;

import lombok.Builder;
import lombok.Data;

/**
 * 学生作业信息表
 * @TableName stu_homework
 */
@TableName(value ="stu_homework")
@Data
@Builder
public class StuHomework {
    /**
     * 作业ID
     */
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
     * 是否完成该作业：1，未提交；2，已提交未批改；3，已提交已批改
     */
    private Integer completeAndCorrect;

    /**
     * 学生提交内容
     */
    private String studentContent;

    /**
     * 提交时间
     */
    private Date submitTime;

    /**
     * 作业得分
     */
    private Double score;

    /**
     * 批改时间
     */
    private Date correctTime;

    /**
     * 逻辑删除：0，已删除；1，未删除
     */
    private Integer logicalDeletion;

    /**
     * 老师评语
     */
    private String teacherComment;
}