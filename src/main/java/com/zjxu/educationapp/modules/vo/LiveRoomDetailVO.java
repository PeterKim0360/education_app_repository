package com.zjxu.educationapp.modules.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@Schema(description = "直播间详细信息VO")
public class LiveRoomDetailVO {
    
    @Schema(description = "直播间主键id")
    private Integer liveId;
    
    @Schema(description = "用户id")
    private Long userId;
    
    @Schema(description = "老师姓名")
    private String teacherName;
    
    @Schema(description = "学科id")
    private Integer subjectId;
    
    @Schema(description = "学科名称")
    private String subjectName;
    
    @Schema(description = "直播间状态：0-未开始，1-正在直播")
    private Integer status;
    
    @Schema(description = "直播间名字")
    private String roomName;
    
    @Schema(description = "直播间描述")
    private String description;
    
    @Schema(description = "rtmp协议的拉流路径")
    private String rtmpUrl;
    
    @Schema(description = "开始时间")
    private Date startTime;
    
    @Schema(description = "直播间关联的班级ID集合")
    private List<Long> classIds;
    
    @Schema(description = "直播间关联的班级名称集合")
    private List<String> classNames;
}
