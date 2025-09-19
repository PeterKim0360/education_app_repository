package com.zjxu.educationapp.modules.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Data
public class StudentLiveRoomVO {
    /**
     * 直播间ID
     */
    @Schema(description = "直播间ID")
    private Integer liveId;

    /**
     * 直播间名称
     */
    @Schema(description = "直播间名称")
    private String roomName;

    /**
     * 班级名称
     */
    @Schema(description = "班级名称")
    private String className;

    /**
     * 老师姓名
     */
    @Schema(description = "老师姓名")
    private String teacherName;

    /**
     * 学科名称
     */
    @Schema(description = "学科名称")
    private String subjectName;

    /**
     * 开始时间
     */
    @Schema(description = "开始时间")
    private Date startTime;

    /**
     * 直播状态：0-未开始，1-正在直播
     */
    @Schema(description = "直播状态：0-未开始，1-正在直播")
    private Integer status;
}
