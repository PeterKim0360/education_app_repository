package com.zjxu.educationapp.modules.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 学校图片展示
 * @TableName school_images
 */
@TableName(value ="school_images")
@Data
public class SchoolImages {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long imageId;

    /**
     * 关联的学校ID
     */
    private Long schoolId;

    /**
     * 校徽
     */
    private String imageUrl;
}