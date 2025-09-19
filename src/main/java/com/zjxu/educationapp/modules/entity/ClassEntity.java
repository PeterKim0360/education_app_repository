package com.zjxu.educationapp.modules.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 班级表
 * @TableName class
 */
@TableName(value ="class")
@Data
public class ClassEntity implements Serializable {
    /**
     * 班级ID
     */
    @TableId(type = IdType.AUTO)
    private Long classId;

    /**
     * 班级名称
     */
    private String className;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}