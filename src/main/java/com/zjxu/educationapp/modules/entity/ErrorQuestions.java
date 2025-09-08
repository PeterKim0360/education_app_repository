package com.zjxu.educationapp.modules.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName error_questions
 */
@TableName(value ="error_questions")
@Data
public class ErrorQuestions {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer questionId;

    /**
     * 关联对应学科的ID
     */
    private Integer subjectId;

    /**
     * 题目内容
     */
    private String questionText;

    /**
     * 是否掌握（0：未掌握，1：已掌握）
     */
    private Boolean isMastered;

    /**
     * 创建时间
     */
    private Date createdTime;

    /**
     * 当前用户ID
     */
    private Long userId;
}