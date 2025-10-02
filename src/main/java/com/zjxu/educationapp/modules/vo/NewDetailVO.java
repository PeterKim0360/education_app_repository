package com.zjxu.educationapp.modules.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;

@Data
public class NewDetailVO {

    /**
     * 新闻标题
     */
    private String title;

    /**
     * 新闻摘要
     */
    private String summary;

    /**
     * 来源
     */
    private String source;

    /**
     * 发布时间
     */
    private Date publishTime;

}
