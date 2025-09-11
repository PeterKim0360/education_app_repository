package com.zjxu.educationapp.modules.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 好友关系表
 * @TableName friend_relation
 */
@TableName(value ="friend_relation")
@Data
public class FriendRelation implements Serializable {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID（主动发起好友关系的用户）
     */
    private Long userId;

    /**
     * 好友ID（被动接受好友关系的用户）
     */
    private Long friendId;

    /**
     * 好友状态：0-待确认，1-已确认，2-已拒绝
     */
    private Integer status;

    /**
     * 好友备注（用户给好友设置的备注名）
     */
    private String remark;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}