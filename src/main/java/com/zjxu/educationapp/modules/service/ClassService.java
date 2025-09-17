package com.zjxu.educationapp.modules.service;

import com.zjxu.educationapp.common.utils.Result;
import com.zjxu.educationapp.modules.entity.Class;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author huawei
* @description 针对表【class(班级表)】的数据库操作Service
* @createDate 2025-09-17 16:22:04
*/
public interface ClassService extends IService<Class> {
    /**
     * 创建班级
     *
     * @param className
     * @param subjectId
     * @return
     */
    Result<?> createClass(String className, Integer subjectId);
}
