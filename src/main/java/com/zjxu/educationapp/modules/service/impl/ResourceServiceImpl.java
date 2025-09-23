package com.zjxu.educationapp.modules.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjxu.educationapp.common.utils.Result;
import com.zjxu.educationapp.modules.entity.Resource;
import com.zjxu.educationapp.modules.service.ResourceService;
import com.zjxu.educationapp.modules.mapper.ResourceMapper;
import com.zjxu.educationapp.modules.vo.ResourceDetailVO;
import com.zjxu.educationapp.modules.vo.ResourceSimpleVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
* @author huawei
* @description 针对表【resource(资源)】的数据库操作Service实现
* @createDate 2025-09-23 12:07:11
*/
@Service
public class ResourceServiceImpl extends ServiceImpl<ResourceMapper, Resource>
    implements ResourceService{
    @Autowired
    private ResourceMapper resourceMapper;

    /**
     * 获取资源列表
     * @return
     */
    @Override
    public Result<List<ResourceSimpleVO>> resourcesSimpleList() {
        List<Resource> resources = resourceMapper.selectList(new LambdaQueryWrapper<Resource>()
                .eq(Resource::getStatus, 1)
                .orderByDesc(Resource::getUpdateTime));
        List<ResourceSimpleVO> resourcesSimpleVOList = new ArrayList<>();
        for (Resource resource : resources) {
            ResourceSimpleVO resourceSimpleVO = ResourceSimpleVO.builder()
                    .id(resource.getId())
                    .title(resource.getTitle())
                    .description(resource.getDescription())
                    .coverUrl(resource.getCoverUrl())
                    .type(resource.getType())
                    .duration(resource.getDuration())
                    .author(resource.getAuthor())
                    .viewCount(resource.getViewCount())
                    .likeCount(resource.getLikeCount())
                    .build();
            resourcesSimpleVOList.add(resourceSimpleVO);
        }
        return Result.ok(resourcesSimpleVOList);
    }

    /**
     * 获取资源详情
     * @param resourceId
     * @return
     */
    @Override
    public Result<ResourceDetailVO> getResourceDetail(Long resourceId) {
        Resource resource = resourceMapper.selectOne(new LambdaQueryWrapper<Resource>()
                .eq(Resource::getId, resourceId)
                .eq(Resource::getStatus, 1)
                .orderByDesc(Resource::getUpdateTime));
            ResourceDetailVO resourceDetailVO = ResourceDetailVO.builder()
                    .id(resource.getId())
                    .title(resource.getTitle())
                    .description(resource.getDescription())
                    .duration(resource.getDuration())
                    .author(resource.getAuthor())
                    .fileUrl(resource.getFileUrl())
                    .viewCount(resource.getViewCount())
                    .likeCount(resource.getLikeCount())
                    .updateTime(resource.getUpdateTime())
                    .build();
        return Result.ok(resourceDetailVO);
    }

}




