package com.zjxu.educationapp.modules.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

/**
 * 填空题
 * @TableName fill_in_blank
 */
@TableName(value ="fill_in_blank")
@Data
@Builder
public class FillInBlank {
    /**
     * 关联错题主表的question_id
     */
    @TableId
    private Integer questionId;

    /**
     * 正确答案（JSON格式）
     */
    private String correctAnswers;

    /**
     * 用户答案（JSON格式）
     */
    private String userAnswers;
}