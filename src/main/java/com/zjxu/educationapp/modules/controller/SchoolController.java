package com.zjxu.educationapp.modules.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zjxu.educationapp.common.utils.MpListPageUtil;
import com.zjxu.educationapp.common.utils.Result;
import com.zjxu.educationapp.modules.entity.ProvinceInfo;
import com.zjxu.educationapp.modules.entity.SchoolInfo;
import com.zjxu.educationapp.modules.service.ProvinceInfoService;
import com.zjxu.educationapp.modules.service.SchoolService;
import com.zjxu.educationapp.modules.vo.SchoolDetailVO;
import com.zjxu.educationapp.modules.vo.SchoolSimpleVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 学校
 */
@Slf4j
@RestController
@RequestMapping(value = "/api/school")
public class SchoolController {
    @Autowired
    private SchoolService schoolService;
    @Autowired
    private ProvinceInfoService provinceInfoService;
    /**
     * 学校分页查询
     */
    @GetMapping
    public Result<IPage<SchoolSimpleVO>> QuerySchoolInfoVO(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "8") int size){
        log.info("学校分页查询，当前页数：{},每页个数：{}",page,size);
        //分页查询
        Page<SchoolInfo> infoPage = schoolService.page(new Page<SchoolInfo>(page, size),
                new QueryWrapper<SchoolInfo>().orderByAsc("school_rank"));
        IPage<SchoolSimpleVO> simpleVOIPage = infoPage.convert(schoolInfo -> {
            SchoolSimpleVO schoolInfoVo = new SchoolSimpleVO();
            BeanUtils.copyProperties(schoolInfo,schoolInfoVo);
            return schoolInfoVo;
        });
        return Result.ok(simpleVOIPage);
    }

    /**
     * 根据学校id查详情
     */
    @GetMapping("/detail")
    public Result<SchoolDetailVO> QuerySchoolInfo(
            @RequestParam("schoolId") Long schoolId,
            @RequestParam(value = "provinceId",required = false) Long provinceId){
        log.info("查看学校详情，学校ID：{}，查询省份ID：{}",schoolId,provinceId);
        if (provinceId==null){
           provinceId = schoolService.getDefaultProvinceId(schoolId);
        }
        //根据省份ID和学校ID查询该学校最低分数
        ProvinceInfo provinceInfo = provinceInfoService.getOne(new QueryWrapper<ProvinceInfo>().eq("school_id", schoolId).eq("province_id",provinceId));
        if (provinceInfo==null){
            log.error("信息还未录入");
            return Result.error();
        }
        //将最低分存入VO
        SchoolDetailVO schoolDetailVO = new SchoolDetailVO();
        //拷贝信息到VO
        BeanUtils.copyProperties(provinceInfo,schoolDetailVO);
        //根据学校ID查询学校详细信息
        SchoolInfo schoolInfo = schoolService.getOne(new QueryWrapper<SchoolInfo>().eq("school_id", schoolId));
        //拷贝信息到VO
        BeanUtils.copyProperties(schoolInfo,schoolDetailVO);
        return Result.ok(schoolDetailVO);
    }

    /**
     * 根据学校名称模糊分页查询
     */
    @GetMapping("/select/name")
    public Result<IPage<SchoolSimpleVO>> QuerySchoolInfoByName(
            @RequestParam("schoolName") String schoolName,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "8") int size){
        log.info("根据名称查看学校详情，搜索名称：{}",schoolName);
        Page<SchoolInfo> infoPage = schoolService.page(new Page<SchoolInfo>(page, size),
                new QueryWrapper<SchoolInfo>().
                        like("school_name", schoolName).orderByAsc("school_rank"));
        IPage<SchoolSimpleVO> voiPage = infoPage.convert(schoolInfo -> {
            SchoolSimpleVO schoolSimpleVO = new SchoolSimpleVO();
            BeanUtils.copyProperties(schoolInfo,schoolSimpleVO);
            return schoolSimpleVO;
        });
        return Result.ok(voiPage);
    }

    /**
     * 根据输入的成绩查看能够录取的学校
     */
    @GetMapping("/select/score")
    public Result<IPage<SchoolSimpleVO>> QuerySchoolInfoByScore(
            @RequestParam("score") Integer score,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "8") int size,
            @RequestParam(value = "provinceId",required = false) Long provinceId){
        log.info("根据成绩查看可能的学校：{}",score);
        if (provinceId==null){
            //默认浙江省
            provinceId=1L;
        }
        //根据省份的ID查询该省所有学校的今年分数线
        List<ProvinceInfo> provinceInfos = provinceInfoService.list(new QueryWrapper<ProvinceInfo>().eq("province_id", provinceId));
        List<SchoolInfo> schoolInfos=new ArrayList<>();
        for (ProvinceInfo provinceInfo : provinceInfos) {
            //满足条件
            if (score<=(provinceInfo.getSchoolScoreThisYear()+10)||score>=(provinceInfo.getSchoolScoreThisYear()-10)){
                SchoolInfo schoolInfo=schoolService.getOne(new QueryWrapper<SchoolInfo>()
                        .eq("school_id",provinceInfo.getSchoolId())
                        .eq("province_id",provinceId));
                log.info("根据条件获取的SchoolInfo对象：{}", schoolInfo);
                if (schoolInfo!= null) {
                    schoolInfos.add(schoolInfo);
                }
            }
        }
        IPage<SchoolInfo> infoIPage = MpListPageUtil.getPage(schoolInfos, page, size);
        IPage<SchoolSimpleVO> simpleVOIPage = infoIPage.convert(schoolInfo -> {
            SchoolSimpleVO schoolSimpleVO = new SchoolSimpleVO();
            BeanUtils.copyProperties(schoolInfo,schoolSimpleVO);
            return schoolSimpleVO;
        });
        return Result.ok(simpleVOIPage);
    }
}
