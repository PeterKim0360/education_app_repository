package com.zjxu.educationapp.modules.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 错题练习会话表
 * @TableName practice_session
 */
@TableName(value ="practice_session")
@Data
public class PracticeSession {
    /**
     * 会话ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 学生ID
     */
    private Long studentId;

    /**
     * 学科ID
     */
    private Integer subjectId;

    /**
     * 题目队列(JSON格式)
     */
    private String questionQueue;

    /**
     * 错题集合(JSON格式)
     */
    private String wrongQuestions;

    /**
     * 当前批次
     */
    private Integer currentBatch;

    /**
     * 总批次数
     */
    private Integer totalBatches;

    /**
     * 是否完成(0-未完成,1-完成)
     */
    private Integer completed;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}