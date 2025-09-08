package com.zjxu.educationapp.modules.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjxu.educationapp.common.constant.Province;
import com.zjxu.educationapp.common.utils.Result;
import com.zjxu.educationapp.modules.entity.Provinces;
import com.zjxu.educationapp.modules.entity.ProvinceInfo;
import com.zjxu.educationapp.modules.service.ProvinceService;
import com.zjxu.educationapp.modules.mapper.ProvinceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
* @author huawei
* @description 针对表【province(省份)】的数据库操作Service实现
* @createDate 2025-09-08 15:44:08
*/
@Service
public class ProvinceServiceImpl extends ServiceImpl<ProvinceMapper, Provinces>
    implements ProvinceService{
    /**
     * 响应省份信息
     * @return
     */
    @Override
    public Result<List<Map<Integer,String>>> responseDeaultProvinces() {
        List<Map<Integer, String>> list = Province.toList();
        return Result.ok(list);
    }
}




