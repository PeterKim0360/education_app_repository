package com.zjxu.educationapp.modules.dto;

import lombok.Data;

import java.util.List;

@Data
public class GroupChatMessageByTeachDTO {
    private List<Long> teamIds;
    private Long fromUserId;
    private String content;
    private Integer messageType;
}
