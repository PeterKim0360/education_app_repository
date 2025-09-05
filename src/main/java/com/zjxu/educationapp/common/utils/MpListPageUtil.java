package com.zjxu.educationapp.common.utils;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

public class MpListPageUtil {
    /**
     * 用MyBatis-Plus的Page对象对List分页
     */
    public static <T> IPage<T> getPage(List<T> sourceList, int pageNum, int pageSize) {
        Page<T> page = new Page<>(pageNum, pageSize);
        // 总记录数
        page.setTotal(sourceList.size());
        // 计算总页数
        page.setPages((long) Math.ceil(sourceList.size() * 1.0 / pageSize));

        // 计算索引并截取数据
        int start = (int) page.offset();
        int end = Math.min((int) (page.offset() + page.getSize()), sourceList.size());
        if (start > end) {
            page.setRecords(List.of());
        } else {
            page.setRecords(sourceList.subList(start, end));
        }
        return page;
    }
}
