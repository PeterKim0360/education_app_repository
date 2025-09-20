package com.zjxu.educationapp.modules.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 
 * @TableName courses
 */
@TableName(value ="courses")
@Data
public class Courses {
    /**
     * 课程表主键
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 课程名
     */
    private String courseName;

    /**
     * 课程描述
     */
    private String description;

    /**
     * 课程号
     */
    private String courseCode;

    /**
     * 老师id
     */
    private Long teacherId;

    /**
     * 学生人数
     */
    private Integer studentCount;

    /**
     * 状态: 0-未上课 1-正在上课 
     */
    private Integer status;
}