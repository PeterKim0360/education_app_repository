package com.zjxu.educationapp.modules.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 学科班级表
 * @TableName subject_class
 */
@TableName(value ="subject_class")
@Data
public class SubjectClass {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 
     */
    private Long classId;

    /**
     * 
     */
    private Integer subjectId;

    /**
     * 状态：0，未被老师占用；1，被老师占用
     */
    private Integer status;

    /**
     * 学生数量
     */
    private Integer studentCount;
}