package com.zjxu.educationapp.modules.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 
 * @TableName subjects
 */
@TableName(value ="subjects")
@Data
public class Subjects {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Integer subjectId;

    /**
     * 学科名称
     */
    private String subjectName;

    /**
     * 学科分类
     */
    private String subjectType;
}