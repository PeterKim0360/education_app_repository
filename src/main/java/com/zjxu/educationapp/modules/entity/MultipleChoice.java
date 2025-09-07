package com.zjxu.educationapp.modules.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

/**
 * 多选表
 * @TableName multiple_choice
 */
@TableName(value ="multiple_choice")
@Data
@Builder
public class MultipleChoice {
    /**
     * 关联错题主表的question_id
     */
    @TableId
    private Integer questionId;

    /**
     * 所有选项（JSON格式）
     */
    private String options;

    /**
     * 正确选项（JSON格式）
     */
    private String correctOptions;

    /**
     * 用户选择的答案（JSON格式）
     */
    private String userAnswer;
}