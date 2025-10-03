package com.zjxu.educationapp.modules.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;

@Data
public class NewSimpleVO {
    /**
     *
     */
    private Long id;

    /**
     * 新闻标题
     */
    private String title;

    /**
     * 封面图片URL
     */
    private String coverUrl;

    /**
     * 来源
     */
    private String source;

    /**
     * 发布时间
     */
    private Date publishTime;

    /**
     * 浏览次数
     */
    private Integer viewCount;

}
