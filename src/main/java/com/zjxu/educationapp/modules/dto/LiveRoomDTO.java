package com.zjxu.educationapp.modules.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class LiveRoomDTO {
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer subjectId;
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String roomName;
    private String description;
    private Date startTime;
    @Schema(description = "班级ID列表",requiredMode = Schema.RequiredMode.REQUIRED)
    private List<Long> classIds;
}
