package com.zjxu.educationapp.modules.vo;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class ResourceDetailVO {
    private Long id;
    private String title;
    private String description;
    private String duration;
    private String author;
    private String fileUrl;
    private Long viewCount;
    private Long likeCount;
    private Date updateTime;

}
