package com.zjxu.educationapp.modules.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "直播间在线用户VO")
public class OnlineUserVO {
    
    @Schema(description = "用户ID")
    private Long userId;
    
    @Schema(description = "用户名")
    private String userName;
    
    @Schema(description = "头像URL")
    private String avatarUrl;
    
    @Schema(description = "身份：0-学生，1-老师")
    private Integer identity;
    
    @Schema(description = "班级名称（学生专用）")
    private String className;
    
    @Schema(description = "进入直播间时间（时间戳）")
    private Long joinTime;
} 