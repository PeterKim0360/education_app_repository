package com.zjxu.educationapp.modules.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@Schema(description = "老师直播间信息VO")
public class TeacherLiveRoomVO {
    
    @Schema(description = "直播间ID")
    private Integer liveId;
    
    @Schema(description = "学科名称")
    private String subjectName;
    
    @Schema(description = "直播间状态：0-未开始，1-正在直播")
    private Integer status;
    
    @Schema(description = "直播间名称")
    private String roomName;
    
    @Schema(description = "直播间描述")
    private String description;
    
    @Schema(description = "开播时间")
    private Date startTime;
    
    @Schema(description = "关联的班级名称集合")
    private List<String> classNames;
}
