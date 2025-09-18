package com.zjxu.educationapp.modules.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 学科-班级-教师表
 * @TableName subject_class_teach
 */
@TableName(value ="subject_class_teach")
@Data
public class SubjectClassTeach {
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

    /**
     * 老师ID
     */
    private Long teachId;
}