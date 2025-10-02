package com.zjxu.educationapp.modules.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 课程表
 * @TableName schedule
 */
@TableName(value ="schedule")
@Data
public class Schedule {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 第几周
     */
    private String week;

    /**
     * 星期几
     */
    private String weekday;

    /**
     * 当前时间
     */
    private String currentTime;

    /**
     * 课程名称
     */
    private String courseName;

    /**
     * 上课时间
     */
    private String courseTime;

    /**
     * 上课地点
     */
    private String location;

    /**
     * 是否为当前课程: 0-不是，1-是
     */
    private Integer isCurrent;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 对应学期
     */
    private Long semesterId;

    /**
     * 课程ID
     */
    private Integer courseId;

    /**
     * 教师ID
     */
    private Long teacherId;


}