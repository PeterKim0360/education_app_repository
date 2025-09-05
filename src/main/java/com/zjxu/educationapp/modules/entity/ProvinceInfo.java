package com.zjxu.educationapp.modules.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 
 * @TableName province_info
 */
@TableName(value ="province_info")
@Data
public class ProvinceInfo {
    /**
     * 省份ID
     */
    private Long provinceId;

    /**
     * 省份名称
     */
    private String provinceName;

    /**
     * 学校ID
     */
    private Long schoolId;

    /**
     * 该校今年最低分
     */
    private Integer schoolScoreThisYear;

    /**
     * 该校去年最低分
     */
    private Integer schoolScoreLastYear;

    /**
     * 该校前年最低分
     */
    private Integer schoolScoreLastLastYear;
}