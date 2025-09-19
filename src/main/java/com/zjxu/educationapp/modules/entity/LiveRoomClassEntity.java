package com.zjxu.educationapp.modules.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName live_room_class
 */
@TableName(value ="live_room_class")
@Data
public class LiveRoomClassEntity implements Serializable {
    /**
     * 直播间关联班级主键id
     */
    @TableId(type = IdType.AUTO)
    private Integer liveRoomClassId;

    /**
     * 班级id
     */
    private Long classId;

    /**
     * 直播间id
     */
    private Integer liveRoomId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}