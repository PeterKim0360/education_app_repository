package com.zjxu.educationapp.modules.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName course_history
 */
@TableName(value ="course_history")
@Data
public class CourseHistory {
    /**
     * 历史课堂记录
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 对应的课程ID
     */
    private Integer subjectId;

    /**
     * 课件，可以是多个，以分号隔开
     */
    private String fileUrl;

    /**
     * 文件描述
     */
    private String fileDescription;

    /**
     * 教师ID
     */
    private Long teacherId;

    /**
     * 上传时间
     */
    private Date uploadTime;
}