package com.zjxu.educationapp.modules.vo;

import lombok.Data;

/**
 * 发送消息请求VO
 */
@Data
public class SendMessageRequestVO {

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
    private Integer messageType = 1;
} 