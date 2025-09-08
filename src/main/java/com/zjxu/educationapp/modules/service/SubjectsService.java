package com.zjxu.educationapp.modules.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zjxu.educationapp.common.utils.Result;
import com.zjxu.educationapp.modules.entity.Subjects;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
* @author huawei
* @description 针对表【subjects】的数据库操作Service
* @createDate 2025-09-08 00:59:25
*/
public interface SubjectsService extends IService<Subjects> {
    /**
     * 错题默认页面响应
     * @return
     */
    Result<IPage<Map<String, Map<Integer, String>>>> responseDefault(int page, int size);
}
