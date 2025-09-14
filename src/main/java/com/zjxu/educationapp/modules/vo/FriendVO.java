package com.zjxu.educationapp.modules.vo;

import lombok.Data;
import java.util.Date;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "好友信息VO")
public class FriendVO {
    /**
     * 好友关系ID
     */
    @Schema(description = "好友关系ID")
    private Long id;

    /**
     * 好友ID
     */
    @Schema(description = "好友ID")
    private Long friendId;

    /**
     * 好友用户名
     */
    @Schema(description = "好友用户名")
    private String friendName;

    /**
     * 好友头像
     */
    @Schema(description = "好友头像")
    private String friendAvatar;

    /**
     * 好友备注
     */
    @Schema(description = "好友备注")
    private String remark;

    /**
     * 成为好友时间
     */
    @Schema(description = "成为好友时间")
    private Date createTime;
}
