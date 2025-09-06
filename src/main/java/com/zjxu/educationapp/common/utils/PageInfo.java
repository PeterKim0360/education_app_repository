package com.zjxu.educationapp.common.utils;

import lombok.Data;

// 分页信息实体类
@Data
public class PageInfo {
    private Integer current; // 当前页码
    private Integer pages;   // 总页数
    private Integer size;    // 每页条数
    private Integer total;   // 总题目数
}