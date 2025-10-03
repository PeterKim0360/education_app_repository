package com.zjxu.educationapp.modules.controller;

import com.zjxu.educationapp.common.utils.Result;
import com.zjxu.educationapp.modules.entity.News;
import com.zjxu.educationapp.modules.entity.Resource;
import com.zjxu.educationapp.modules.service.NewsService;
import com.zjxu.educationapp.modules.service.ResourceService;
import com.zjxu.educationapp.modules.vo.NewDetailVO;
import com.zjxu.educationapp.modules.vo.NewSimpleVO;
import com.zjxu.educationapp.modules.vo.ResourceDetailVO;
import com.zjxu.educationapp.modules.vo.ResourceSimpleVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    @Autowired
    private NewsService newsService;

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

    /**
     * 点赞
     */
    @Operation(summary = "点赞",description = "传参：resourceId")
    @GetMapping("/like")
    public Result<?> like(Long resourceId){
        log.info("点赞:{}",resourceId);
        Resource resource = resourceService.getById(resourceId);
        resource.setLikeCount(resource.getLikeCount()+1);
        resourceService.updateById(resource);
        return Result.ok();
    }

    /**
     * 取消点赞
     */
    @Operation(summary = "取消点赞",description = "传参：resourceId")
    @GetMapping("/cancelLike")
    public Result<?> cancelLike(Long resourceId){
        log.info("取消点赞:{}",resourceId);
        Resource resource = resourceService.getById(resourceId);
        resource.setLikeCount(resource.getLikeCount()-1);
        resourceService.updateById(resource);
        return Result.ok();
    }

    /**
     * 获取新闻列表
     */
    @Operation(summary = "获取新闻列表")
    @GetMapping("/news")
    public Result<List<NewSimpleVO>> newsList(){
        log.info("获取新闻列表");
        List<News> news = newsService.list();
        if (news == null){
            return Result.error("新闻列表为空");
        }
        List<NewSimpleVO> simpleVOList = news.stream().map(news1 -> {
            NewSimpleVO newSimpleVO = new NewSimpleVO();
            BeanUtils.copyProperties(news1, newSimpleVO);
            return newSimpleVO;
        }).toList();
        return Result.ok(simpleVOList);
    }

    /**
     * 获取新闻详情
     */
    @Operation(summary = "获取新闻详情",description = "传参：newsId")
    @GetMapping("/news/detail")
    public Result<NewDetailVO> newsDetail(@RequestParam("newsId") Long newsId){
        log.info("获取新闻详情:{}",newsId);
        if (newsId == null){
            return Result.error("参数错误");
        }
        News news = newsService.getById(newsId);
        if (news == null){
            return Result.error("新闻不存在");
        }
        news.setViewCount(news.getViewCount()+1);
        newsService.updateById(news);
        NewDetailVO newDetailVO = new NewDetailVO();
        BeanUtils.copyProperties(news, newDetailVO);
        return Result.ok(newDetailVO);
    }
}
