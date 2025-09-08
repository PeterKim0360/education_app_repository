package com.zjxu.educationapp.modules.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 省份
 * @TableName province
 */
@TableName(value ="province")
@Data
public class Provinces {
    /**
     * 省份ID
     */
    private Integer provinceId;

    /**
     * 省份名称
     */
    private String provinceName;
}