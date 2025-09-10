package com.zjxu.educationapp.modules.vo;

import lombok.Data;

@Data
public class CelebrityDetailVO {
    private Long celebrityId;
    private String celebrityName;
    private String profession;
    private String era;
    private String description;
    private String avatarUrl;
}
