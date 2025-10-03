package com.zjxu.educationapp.modules.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.SneakyThrows;

import java.util.Date;
import java.util.List;

@Data
public class HomeworkInClassVO {

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
    private List<String> workUrls;

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

}
