package com.zjxu.educationapp.modules.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zjxu.educationapp.common.utils.Result;
import com.zjxu.educationapp.modules.service.CelebrityService;
import com.zjxu.educationapp.modules.vo.CelebrityDetailVO;
import com.zjxu.educationapp.modules.vo.CelebritySimpleVO;
import com.zjxu.educationapp.modules.vo.UserFavoriteCelebrityVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

/**
 * 名人相关接口
 */
@RestController
@RequestMapping("/api/celebrity")
@Slf4j
@Tag(name = "名人相关接口")
public class CelebrityController {
    @Autowired
    private CelebrityService celebrityService;

    /**
     * 名人分页查询（含根据职位/身份模糊查询）
     */
    @Operation(summary = "名人分页查询（含根据职位/身份模糊查询）",description = "可选：profession,page,size")
    @GetMapping
    public Result<IPage<CelebritySimpleVO>> queryCelebritySimpleInfo(
            @RequestParam String profession,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "5") Integer size){
        log.info("名人分页查询（含根据职位/身份模糊查询）");
        return celebrityService.queryCelebritySimpleInfo(profession,page,size);
    }

    /**
     * 根据ID查名人详情
     */
    @Operation(summary = "根据ID查名人详情",description = "传参：celebrityId")
    @GetMapping("detail")
    public Result<CelebrityDetailVO> queryCelebrityDetailInfo(@RequestParam("celebrityId") Long celebrityId){
        log.info("根据ID查名人详情,ID为：{}",celebrityId);
        return celebrityService.queryCelebrityDetailInfo(celebrityId);
    }

    /**
     * 用户关注的名人列表
     */
    @Operation(summary = "用户关注的名人列表",description = "可选：page,size")
    @GetMapping("/user/favorites")
    public Result<IPage<UserFavoriteCelebrityVO>> queryFavorites(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "5") Integer size){
        log.info("用户关注的名人列表查询");
        return celebrityService.queryFavorites(page,size);
    }

    /**
     * 加入当前用户关注列表
     */
    @Operation(summary = "加入当前用户关注列表",description = "传参：celebrityId")
    @PostMapping("/user/favorites/insert")
    @Transactional
    public Result<?> addFavorite(@RequestParam("celebrityId") Long celebrityId){
        log.info("加入当前用户关注列表的名人ID：{}",celebrityId);
        return celebrityService.addFavorite(celebrityId);
    }

    /**
     * 取消关注
     */
    @Operation(summary = "取消关注")
    @DeleteMapping("/unfollow")
    @Transactional
    public Result<?> unFollow(@RequestParam("celebrityId") Long celebrityId){
        log.info("取消关注,该名人ID为：{}",celebrityId);
        return celebrityService.unFollow(celebrityId);
    }

}
