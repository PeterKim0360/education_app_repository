package com.zjxu.educationapp.modules.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zjxu.educationapp.modules.entity.SchoolInfo;

public interface SchoolService extends IService<SchoolInfo> {
    /**
     * 获取默认省份ID
     * @param schoolId
     * @return
     */
    Long getDefaultProvinceId(Long schoolId);
}
