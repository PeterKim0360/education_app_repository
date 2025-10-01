package com.zjxu.educationapp.modules.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class HomeworkInClassStuVO {

    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 课程id
     */
    private Integer subjectId;

    /**
     * 小练urls
     */
    private List<String> workUrls;

    /**
     * 作业标题
     */
    private String title;

    /**
     * 截止时间
     */
    private Date deadTime;
}
