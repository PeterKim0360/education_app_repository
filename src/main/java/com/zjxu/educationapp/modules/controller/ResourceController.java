package com.zjxu.educationapp.modules.controller;

import com.zjxu.educationapp.common.utils.Result;
import com.zjxu.educationapp.modules.service.ResourceService;
import com.zjxu.educationapp.modules.vo.ResourceDetailVO;
import com.zjxu.educationapp.modules.vo.ResourceSimpleVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 资源相关接口
 */
@RestController
@RequestMapping("/api/resource")
@Slf4j
@Tag(name = "资源相关接口")
public class ResourceController {
    @Autowired
    private ResourceService resourceService;

    /**
     * 获取资源列表
     */
    @Operation(summary = "获取资源列表")
    @GetMapping("/list")
    public Result<List<ResourceSimpleVO>> resourcesSimpleList(){
        log.info("获取资源列表");
        return resourceService.resourcesSimpleList();
    }

    /**
     * 获取资源详情
     */
    @Operation(summary = "获取资源详情",description = "传参：resourceId")
    @GetMapping("/detail")
    public Result<ResourceDetailVO> resourcesDetail(Long resourceId){
        log.info("获取资源详情:{}",resourceId);
        return resourceService.getResourceDetail(resourceId);
    }
}
