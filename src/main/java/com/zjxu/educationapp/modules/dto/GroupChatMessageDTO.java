package com.zjxu.educationapp.modules.dto;

import lombok.Data;

@Data
public class GroupChatMessageDTO {
    private Long teamId;
    private Long fromUserId;
    private String content;
    private Integer messageType;
}
