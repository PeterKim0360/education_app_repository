package com.zjxu.educationapp.modules.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 学生班级表
 * @TableName student_class
 */
@TableName(value ="student_class")
@Data
public class StudentClassEntity implements Serializable {
    /**
     * 学生信息主键
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

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}