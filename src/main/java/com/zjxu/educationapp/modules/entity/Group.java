package com.zjxu.educationapp.modules.entity;

import lombok.Data;

@Data
public class Group {
    /**
     * 小组名
     */
    private String name;

    /**
     * 理想人数
     */
    private Integer capacity;

    /**
     * 0-进行中 1-锁定(禁止再加人)
     */
    private Integer status;

    /**
     * 创建者（老师id）
     */
    private Long createdBy;
}
