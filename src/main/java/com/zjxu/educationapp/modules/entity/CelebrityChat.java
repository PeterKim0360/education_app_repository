package com.zjxu.educationapp.modules.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;

import lombok.Builder;
import lombok.Data;

/**
 * 名人对话记录
 * @TableName celebrity_chat
 */
@TableName(value ="celebrity_chat")
@Data
@Builder
public class CelebrityChat {
    /**
     * 对话ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 名人ID
     */
    private Long celebrityId;

    /**
     * 会话ID，用于区分同一用户与同一名人的不同会话
     */
    private String sessionId;

    /**
     * 
     */
    private String content;

    /**
     * 发送者类型：1-用户 2-AI名人
     */
    private Integer senderType;

    /**
     * 发送时间
     */
    private Date sendTime;
}