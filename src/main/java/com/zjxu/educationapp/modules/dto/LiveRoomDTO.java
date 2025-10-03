package com.zjxu.educationapp.modules.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class LiveRoomDTO {
    @Schema(description = "科目ID", required = true)
    private Integer subjectId;
    @Schema(description = "房间名称", required = true)
    private String roomName;
    private String description;
    private Date startTime;
    @Schema(description = "班级ID列表", required = true)
    private List<Long> classIds;
}
