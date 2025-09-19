package com.zjxu.educationapp.modules.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "用户信息VO")
public class UserInfoVO {
    
    @Schema(description = "用户ID")
    private Long id;
    
    @Schema(description = "用户名")
    private String userName;
    
    @Schema(description = "头像URL")
    private String avatarUrl;
    
    @Schema(description = "手机号")
    private String phone;
    
    @Schema(description = "个人简介")
    private String profile;
    
    @Schema(description = "状态：0禁用，1启用")
    private Integer status;

    @Schema(description = "身份：0-学生，1-老师")
    private Integer identity;

    @Schema(description = "班级名称")
    private String className;
}
