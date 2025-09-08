package com.zjxu.educationapp.modules.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zjxu.educationapp.common.utils.MpListPageUtil;
import com.zjxu.educationapp.common.utils.Result;
import com.zjxu.educationapp.modules.entity.ProvinceInfo;
import com.zjxu.educationapp.modules.entity.SchoolInfo;
import com.zjxu.educationapp.modules.service.ProvinceInfoService;
import com.zjxu.educationapp.modules.service.ProvinceService;
import com.zjxu.educationapp.modules.service.SchoolService;
import com.zjxu.educationapp.modules.vo.SchoolDetailVO;
import com.zjxu.educationapp.modules.vo.SchoolSimpleVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 搜索学校相关接口
 */
@Tag(name = "搜索学校相关接口")
@Slf4j
@RestController
@RequestMapping(value = "/api/school")
public class SchoolController {
    @Autowired
    private SchoolService schoolService;
    @Autowired
    private ProvinceService provinceService;
    /**
     * 学校分页查询（含模糊）
     */
    @Operation(summary = "学校分页查询",description = "可选：page,size")
    @GetMapping
    public Result<IPage<SchoolSimpleVO>> QuerySchoolInfoVO(
            @RequestParam(value = "schoolName",required = false) String schoolName,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "8") int size){
        log.info("学校分页查询，用户输入：{},当前页数：{},每页个数：{}",schoolName,page,size);
        return schoolService.querySchoolInfoVO(schoolName,page,size);
    }

    /**
     * 根据学校id查详情
     */
    @Operation(summary = "根据学校id查详情",description = "传参：schoolId;可选：provinceId")
    @GetMapping("/detail")
    public Result<SchoolDetailVO> QuerySchoolInfo(
            @RequestParam("schoolId") Long schoolId,
            @RequestParam(value = "provinceId",required = false,defaultValue = "1") Long provinceId){
        log.info("查看学校详情，学校ID：{}，查询省份ID：{}",schoolId,provinceId);
        return schoolService.queryDetail(schoolId,provinceId);
    }

    /**
     * 根据输入的成绩查看可能录取的学校
     */
    @Operation(summary = "根据输入的成绩查看可能录取的学校",description = "传参：score;可选：page,size,provinceId")
    @GetMapping("/select/score")
    public Result<IPage<SchoolSimpleVO>> QuerySchoolInfoByScore(
            @RequestParam("score") Integer score,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "8") int size,
            @RequestParam(value = "provinceId",required = false,defaultValue = "1") Long provinceId){
        log.info("根据成绩查看可能的学校：{}",score);
        return schoolService.queryByScore(score,page,size,provinceId);
    }

    /**
     * 响应省份信息
     */
    @Operation(summary = "响应省份信息")
    @GetMapping("/province")
    public Result<List<Map<Integer,String >>> responseDefault(){
        return provinceService.responseDeaultProvinces();
    }
}
