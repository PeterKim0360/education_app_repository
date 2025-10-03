package com.zjxu.educationapp.modules.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;

@Data
public class GroupingPlanDTO {

    /**
     * 目标队伍数量
     */
    private Integer targetTeamCount;

    /**
     * 1 允许自由组队，0 反之
     */
    private Integer allowFreeJoin;

    /**
     * 队伍下限数量
     */
    private Integer minTeamSize;

    /**
     * 0-进行中；1-已锁定
     */
    private Integer status;

    /**
     * 创建者id
     */
    private Long createdBy;

}
