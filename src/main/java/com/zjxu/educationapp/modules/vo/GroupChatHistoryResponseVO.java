package com.zjxu.educationapp.modules.vo;

import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupChatHistoryResponseVO {

    private List<MessageVO> messages;
    private Integer pageNum;
    private Integer pageSize;
    private Long total;
    private Integer totalPages;
    private Boolean hasMore;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MessageVO {
        private Long id;
        private Long fromUserId;
        private String content;
        private Integer messageType;
        private Date sendTime;
    }
}



