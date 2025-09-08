package com.zjxu.educationapp.modules.service;

import com.zjxu.educationapp.common.utils.Result;
import com.zjxu.educationapp.modules.entity.Provinces;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zjxu.educationapp.modules.entity.ProvinceInfo;

import java.util.List;
import java.util.Map;

/**
* @author huawei
* @description 针对表【province(省份)】的数据库操作Service
* @createDate 2025-09-08 15:44:08
*/
public interface ProvinceService extends IService<Provinces> {
    /**
     * 响应省份信息
     * @return
     */
    Result<List<Map<Integer,String>>> responseDeaultProvinces();

}
