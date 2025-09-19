package com.zjxu.educationapp.modules.vo;

import lombok.Data;
import java.util.Date;

@Data
public class FriendApplicationVO {
    /**
     * 申请ID
     */
    private Long id;

    /**
     * 申请人ID
     */
    private Long applicantId;

    /**
     * 申请人用户名
     */
    private String applicantName;

    /**
     * 申请人头像
     */
    private String applicantAvatar;

    /**
     * 申请备注
     */
    private String remark;

    /**
     * 申请状态：0-待确认，1-已确认，2-已拒绝
     */
    private Integer status;

    /**
     * 申请时间
     */
    private Date createTime;
}
