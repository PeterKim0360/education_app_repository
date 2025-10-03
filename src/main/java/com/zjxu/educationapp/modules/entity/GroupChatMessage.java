package com.zjxu.educationapp.modules.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.util.Date;

import lombok.Builder;
import lombok.Data;

/**
 * 小组讨论区消息表
 * @TableName group_chat_message
 */
@TableName(value ="group_chat_message")
@Data
@Builder
public class GroupChatMessage {
    /**
     * 消息id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 小组id
     */
    private Long teamId;

    /**
     * 发送者id
     */
    private Long fromUserId;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 消息类型：1-文字 2-图片 3-文件
     */
    private Integer messageType;

    /**
     * 发送时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date sendTime;
}