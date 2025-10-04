package com.zjxu.educationapp.modules.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * WebSocket消息VO
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WebSocketMessageVO {

    /**
     * 消息内容
     */
    private String content;

    /**
     * 消息类型：1-文字消息 2-图片消息 3-文件消息
     */
    private Integer messageType = 1;

    /**
     * 消息ID（发送后返回）
     */
    private Long messageId;

    /**
     * 批量消息ID（发送后返回）
     */
    private List<Long> messageIds;

    /**
     * 发送时间戳
     */
    private Long timestamp;

    /**
     * 消息状态：success-成功 error-失败
     */
    private String status;

    /**
     * 错误信息（如果发送失败）
     */
    private String errorMsg;
} 