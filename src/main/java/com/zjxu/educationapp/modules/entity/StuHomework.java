package com.zjxu.educationapp.modules.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 学生作业信息表
 * @TableName stu_homework
 */
@TableName(value ="stu_homework")
@Data
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
     * 作业名称
     */
    private String homeworkName;

    /**
     * 截止时间
     */
    private Date deadTime;

    /**
     * 发送时间
     */
    private Date sendTime;
}