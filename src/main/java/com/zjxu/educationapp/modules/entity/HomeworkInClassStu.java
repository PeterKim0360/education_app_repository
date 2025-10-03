package com.zjxu.educationapp.modules.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 学生随堂测表
 * @TableName homework_in_class_stu
 */
@TableName(value ="homework_in_class_stu")
@Data
public class HomeworkInClassStu {
    private Long id;

    /**
     * 课程id
     */
    private Integer subjectId;

    /**
     * 学生id
     */
    private Long studentId;

    /**
     * 小练urls
     */
    private String workUrls;

    /**
     * 作业标题
     */
    private String title;

    /**
     * 截止时间
     */
    private Date deadTime;
}