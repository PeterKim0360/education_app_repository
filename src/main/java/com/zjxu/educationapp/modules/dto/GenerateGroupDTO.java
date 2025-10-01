package com.zjxu.educationapp.modules.dto;

import lombok.Data;

@Data
public class GenerateGroupDTO {

    /**
     * 课程id
     */
    private Integer subjectId;

    /**
     * 目标队伍数量
     */
    private Integer targetTeamCount;

}
