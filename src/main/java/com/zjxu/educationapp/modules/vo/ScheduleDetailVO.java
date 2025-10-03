package com.zjxu.educationapp.modules.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;

@Data
public class ScheduleDetailVO {

    /**
     * 第几周
     */
    private String week;

    /**
     * 星期几
     */
    private String weekday;

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
     * 课程ID
     */
    private Integer courseId;

    /**
     * 教师名
     */
    private String teachName;
}
