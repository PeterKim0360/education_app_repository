package com.zjxu.educationapp.modules.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class UserPostDTO {

    @Schema(description = "动态标题")
    private String title;
    
    @Schema(description = "动态内容")
    private String content;
    
    @Schema(description = "图片URL列表")
    private List<String> contentImageUrls;
}
