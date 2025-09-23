package com.zjxu.educationapp.modules.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 用户聊天消息VO
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserChatMessageVO {

    /**
     * 消息ID
     */
    private Long id;

    /**
     * 发送者ID
     */
    private Long fromUserId;

    /**
     * 发送者用户名
     */
    private String fromUserName;

    /**
     * 发送者头像
     */
    private String fromUserAvatar;

    /**
     * 接收者ID
     */
    private Long toUserId;

    /**
     * 接收者用户名
     */
    private String toUserName;

    /**
     * 接收者头像
     */
    private String toUserAvatar;

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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date sendTime;

    /**
     * 是否是当前用户发送的消息
     */
    private Boolean isSentByCurrentUser;
} 