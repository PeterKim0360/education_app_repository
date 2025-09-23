package com.zjxu.educationapp.modules.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResourceSimpleVO {
    private Long id;
    private String title;
    private String description;
    private String coverUrl;
    private String type;
    private String duration;
    private String author;
    private Long viewCount;
    private Long likeCount;
}
