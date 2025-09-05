package com.zjxu.educationapp.modules.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 学校信息
 * @TableName school_info
 */
@TableName(value ="school_info")
@Data
public class SchoolInfo {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long schoolId;

    /**
     * 学校名
     */
    private String schoolName;

    /**
     * 简略地址
     */
    private String simpleAddress;

    /**
     * 详细地址
     */
    private String detailedAddress;

    /**
     * 校徽图片的 URL / 路径
     */
    private String emblemUrl;

    /**
     * 学校简介
     */
    private String schoolProfile;

    /**
     * 是否为985（1：是，0：不是）
     */
    @TableField(value = "is_985")
    private Boolean is985;

    /**
     * 是否为211（1：是，0：不是）
     */
    @TableField(value = "is_211")
    private Boolean is211;

    /**
     * 学校排名
     */
    private Integer schoolRank;

    /**
     * 省份ID（1为浙江省）
     */
    private Long provinceId;
}