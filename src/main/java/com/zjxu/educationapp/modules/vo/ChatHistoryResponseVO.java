package com.zjxu.educationapp.modules.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatHistoryResponseVO {
    private List<UserChatMessageVO> messages;
    private Integer pageNum;
    private Integer pageSize;
    private Long total;
    private Integer totalPages;
    private Boolean hasMore;
    private UserInfoVO otherUser;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfoVO {
        private Long userId;
        private String userName;
        private String avatarUrl;
        private Integer identity;
    }
}
