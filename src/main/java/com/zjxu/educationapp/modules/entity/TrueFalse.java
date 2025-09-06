package com.zjxu.educationapp.modules.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 判断题
 * @TableName true_false
 */
@TableName(value ="true_false")
@Data
public class TrueFalse {
    /**
     * 关联错题主表的question_id
     */
    @TableId
    private Integer questionId;

    /**
     * 正确结果（0：错误，1：正确）
     */
    private Boolean correctResult;

    /**
     * 用户答案（0：错误，1：正确）
     */
    @TableField(value = "user_answer")
    private Boolean TrueFalseUserAnswer;
}