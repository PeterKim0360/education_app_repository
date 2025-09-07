package com.zjxu.educationapp.modules.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

/**
 * 单选表
 * @TableName single_choice
 */
@TableName(value ="single_choice")
@Data
@Builder
public class SingleChoice {
    /**
     * 关联错题主表的question_id
     */
    @TableId
    private Integer questionId;

    /**
     * 选项A
     */
    private String optionA;

    /**
     * 选项B
     */
    private String optionB;

    /**
     * 选项C
     */
    private String optionC;

    /**
     * 选项D
     */
    private String optionD;

    /**
     * 单选的正确选项
     */
    private String correctOption;

    /**
     * 用户答案
     */
    private String userAnswer;
}