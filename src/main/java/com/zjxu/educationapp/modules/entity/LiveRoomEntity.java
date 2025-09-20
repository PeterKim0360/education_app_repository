package com.zjxu.educationapp.modules.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 直播间实体
 * @TableName live_room
 */
@TableName(value ="live_room")
@Data
public class LiveRoomEntity implements Serializable {
    /**
     * 直播间主键id
     */
    @TableId(type = IdType.AUTO)
    private Integer liveId;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 学科id
     */
    private Integer subjectId;

    /**
     * 0-未开始，1-正在直播
     */
    private Integer status;

    /**
     * 直播间名字
     */
    private String roomName;

    /**
     * 直播间描述
     */
    private String description;

    /**
     * 推流码
     */
    private String streamKey;

    /**
     * rtmp协议的拉流路径
     */
    private String rtmpUrl;

    /**
     * 观看人数
     */
    private Integer viewerCount;

    /**
     * 开始时间
     */
    private Date startTime;

    /**
     * 直播间创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}