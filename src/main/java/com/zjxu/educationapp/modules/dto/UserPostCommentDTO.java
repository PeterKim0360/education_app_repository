package com.zjxu.educationapp.modules.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "用户动态评论DTO")
public class UserPostCommentDTO {

    @Schema(description = "动态ID")
    private Integer postId;

    @Schema(description = "评论内容")
    private String content;
}
