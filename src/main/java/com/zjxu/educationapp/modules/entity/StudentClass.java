package com.zjxu.educationapp.modules.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.List;

/**
 * 学生班级表
 * @TableName student_class
 */
@TableName(value ="student_class")
@Data
public class StudentClass {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long scId;

    /**
     * 学生ID
     */
    private Long studentId;

    /**
     * 班级ID
     */
    private Long classId;
}