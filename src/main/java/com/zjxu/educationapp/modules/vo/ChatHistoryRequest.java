package com.zjxu.educationapp.modules.vo;

import lombok.Data;

/**
 * 聊天历史查询请求
 */
@Data
public class ChatHistoryRequest {

    /**
     * 对方用户ID（聊天对象）
     */
    private Long otherUserId;

    /**
     * 页码，从1开始
     */
    private Integer pageNum = 1;

    /**
     * 每页大小
     */
    private Integer pageSize = 20;
} 