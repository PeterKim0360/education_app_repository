package com.zjxu.educationapp.modules.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户聊天消息记录
 * @TableName user_chat_message
 */
@TableName(value = "user_chat_message")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserChatMessage implements Serializable {
    
    /**
     * 消息ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 发送者ID
     */
    private Long fromUserId;

    /**
     * 接收者ID
     */
    private Long toUserId;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 消息类型：1-文字消息 2-图片消息 3-文件消息
     */
    private Integer messageType;

    /**
     * 消息状态：0-未读 1-已读
     */
    private Integer status;

    /**
     * 发送时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date sendTime;

    /**
     * 会话标识，用于快速查询两用户间的聊天记录
     * 格式：较小userId:较大userId 确保唯一性
     */
    private String conversationId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
} 