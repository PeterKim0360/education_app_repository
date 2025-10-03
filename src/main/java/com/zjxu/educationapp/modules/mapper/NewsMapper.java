package com.zjxu.educationapp.modules.mapper;

import com.zjxu.educationapp.modules.entity.News;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author huawei
* @description 针对表【news(新闻表)】的数据库操作Mapper
* @createDate 2025-10-02 12:37:43
* @Entity com.zjxu.educationapp.modules.entity.News
*/
@Mapper
public interface NewsMapper extends BaseMapper<News> {

}




