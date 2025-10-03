package com.zjxu.educationapp.modules.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjxu.educationapp.modules.entity.News;
import com.zjxu.educationapp.modules.service.NewsService;
import com.zjxu.educationapp.modules.mapper.NewsMapper;
import org.springframework.stereotype.Service;

/**
* @author huawei
* @description 针对表【news(新闻表)】的数据库操作Service实现
* @createDate 2025-10-02 12:37:43
*/
@Service
public class NewsServiceImpl extends ServiceImpl<NewsMapper, News>
    implements NewsService{

}




