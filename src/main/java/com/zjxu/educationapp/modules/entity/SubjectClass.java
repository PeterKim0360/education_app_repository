package com.zjxu.educationapp.modules.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.List;

/**
 * 学科-班级表
 * @TableName subject_class
 */
@TableName(value ="subject_class")
@Data
public class SubjectClass {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long scId;

    /**
     * 学科ID
     */
    private Integer subjectId;

    /**
     * 班级ID
     */
    private Long classId;
}