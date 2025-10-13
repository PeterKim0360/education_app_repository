package com.zjxu.educationapp.modules.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 资源
 * @TableName resource
 */
@TableName(value ="resource")
@Data
public class Resource {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 描述
     */
    private String description;

    /**
     * 封面图片
     */
    private String coverUrl;

    /**
     * 类型
     */
    private String type;

    /**
     * 持续时间
     */
    private String duration;

    /**
     * 创建者
     */
    private String author;

    /**
     * 浏览量
     */
    private Long viewCount;

    /**
     * 点赞量
     */
    private Long likeCount;

    /**
     * 文件url
     */
    private String fileUrl;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 逻辑删除：0，删除；1，启用
     */
    private Integer status;

    /**
     * 板块类型：1-猜你喜欢；2-资源
     */
    private Integer modelType;
}