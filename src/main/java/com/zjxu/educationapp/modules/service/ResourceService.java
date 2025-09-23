package com.zjxu.educationapp.modules.service;

import com.zjxu.educationapp.common.utils.Result;
import com.zjxu.educationapp.modules.entity.Resource;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zjxu.educationapp.modules.vo.ResourceDetailVO;
import com.zjxu.educationapp.modules.vo.ResourceSimpleVO;

import java.util.List;

/**
* @author huawei
* @description 针对表【resource(资源)】的数据库操作Service
* @createDate 2025-09-23 12:07:11
*/
public interface ResourceService extends IService<Resource> {

    /**
     * 获取资源列表
     * @return
     */
    Result<List<ResourceSimpleVO>> resourcesSimpleList();

    /**
     * 获取资源详情
     * @param resourceId
     * @return
     */
    Result<ResourceDetailVO> getResourceDetail(Long resourceId);
}
