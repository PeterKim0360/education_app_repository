package com.zjxu.educationapp.modules.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjxu.educationapp.modules.entity.SchoolInfo;
import com.zjxu.educationapp.modules.mapper.SchoolInfoMapper;
import com.zjxu.educationapp.modules.service.SchoolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SchoolServiceImpl extends ServiceImpl<SchoolInfoMapper, SchoolInfo>
        implements SchoolService {
    @Autowired
    private SchoolInfoMapper schoolInfoMapper;
    /**
     * 获取默认省份ID
     * @param schoolId
     * @return
     */
    @Override
    public Long getDefaultProvinceId(Long schoolId) {
        return schoolInfoMapper.selectById(schoolId).getProvinceId();
    }
}
