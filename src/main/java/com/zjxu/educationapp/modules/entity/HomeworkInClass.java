package com.zjxu.educationapp.modules.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 随堂作业
 * @TableName homework_in_class
 */
@TableName(value ="homework_in_class")
@Data
public class HomeworkInClass {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 课程id
     */
    private Integer subjectId;

    /**
     * 教师id
     */
    private Long teacherId;

    /**
     * 小练urls
     */
    private String workUrls;

    /**
     * 作业标题
     */
    private String title;

    /**
     * 发布时间
     */
    private Date publishTime;

    /**
     * 截止时间
     */
    private Date deadTime;

    /**
     * 状态：0，草稿；1，发布
     */
    private Integer status;

    /**
     * 更新时间
     */
    private Date updateTime;
}