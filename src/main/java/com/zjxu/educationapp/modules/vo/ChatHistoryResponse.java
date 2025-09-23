package com.zjxu.educationapp.modules.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 聊天历史查询响应
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatHistoryResponse {

    /**
     * 聊天记录列表
     */
    private List<UserChatMessageVO> messages;

    /**
     * 当前页码
     */
    private Integer pageNum;

    /**
     * 每页大小
     */
    private Integer pageSize;

    /**
     * 总记录数
     */
    private Long total;

    /**
     * 总页数
     */
    private Integer totalPages;

    /**
     * 是否还有更多数据
     */
    private Boolean hasMore;

    /**
     * 对方用户信息
     */
    private UserInfoVO otherUser;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserInfoVO {
        private Long userId;
        private String userName;
        private String avatarUrl;
        private Integer identity; // 0-学生，1-老师
    }
} 