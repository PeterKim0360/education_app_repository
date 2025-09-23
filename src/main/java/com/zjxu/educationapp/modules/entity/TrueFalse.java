package com.zjxu.educationapp.modules.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

/**
 * 判断题
 * @TableName true_false
 */
@TableName(value ="true_false")
@Data
@Builder
public class TrueFalse {
    /**
     * 关联错题主表的question_id
     */
    private Integer questionId;

    /**
     * 正确结果（0：错误，1：正确）
     */
    private String correctResult;

    /**
     * 用户答案（0：错误，1：正确）
     */
    private String userAnswer;

    /**
     * 选项
     */
    private String options;

    /**
     * 学科ID
     */
    private Integer subjectId;

    /**
     * 用户ID
     */
    private Long userId;
}