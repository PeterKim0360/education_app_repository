package com.zjxu.educationapp.modules.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjxu.educationapp.common.utils.MpListPageUtil;
import com.zjxu.educationapp.common.utils.Result;
import com.zjxu.educationapp.modules.entity.ProvinceInfo;
import com.zjxu.educationapp.modules.entity.SchoolInfo;
import com.zjxu.educationapp.modules.mapper.ProvinceInfoMapper;
import com.zjxu.educationapp.modules.mapper.SchoolInfoMapper;
import com.zjxu.educationapp.modules.service.SchoolService;
import com.zjxu.educationapp.modules.vo.SchoolDetailVO;
import com.zjxu.educationapp.modules.vo.SchoolSimpleVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SchoolServiceImpl extends ServiceImpl<SchoolInfoMapper, SchoolInfo>
        implements SchoolService {
    @Autowired
    private SchoolInfoMapper schoolInfoMapper;
    @Autowired
    private ProvinceInfoMapper provinceInfoMapper;

    /**
     * 学校分页查询（含模糊）
     * @param schoolName
     * @param page
     * @param size
     * @return
     */
    @Override
    public Result<IPage<SchoolSimpleVO>> querySchoolInfoVO(String schoolName, Integer page, Integer size) {
        //分页查询
        Page<SchoolInfo> infoPage = schoolInfoMapper.selectPage(new Page<SchoolInfo>(page, size),
                new QueryWrapper<SchoolInfo>().orderByAsc("school_rank").like("school_name",schoolName));
        IPage<SchoolSimpleVO> simpleVOIPage = infoPage.convert(schoolInfo -> {
            SchoolSimpleVO schoolInfoVo = new SchoolSimpleVO();
            BeanUtils.copyProperties(schoolInfo,schoolInfoVo);
            return schoolInfoVo;
        });
        return Result.ok(simpleVOIPage);
    }

    /**
     * 根据学校id查详情
     * @param schoolId
     * @param provinceId
     * @return
     */
    @Override
    public Result<SchoolDetailVO> queryDetail(Long schoolId, Long provinceId) {
        //根据省份ID和学校ID查询该学校最低分数
        ProvinceInfo provinceInfo = provinceInfoMapper.selectOne(new QueryWrapper<ProvinceInfo>().eq("school_id", schoolId).eq("province_id",provinceId));
        if (provinceInfo==null){
            log.error("信息还未录入");
            return Result.error();
        }
        //将最低分存入VO
        SchoolDetailVO schoolDetailVO = new SchoolDetailVO();
        //拷贝信息到VO
        BeanUtils.copyProperties(provinceInfo,schoolDetailVO);
        //根据学校ID查询学校详细信息
        SchoolInfo schoolInfo = schoolInfoMapper.selectOne(new QueryWrapper<SchoolInfo>().eq("school_id", schoolId));
        //拷贝信息到VO
        BeanUtils.copyProperties(schoolInfo,schoolDetailVO);
        return Result.ok(schoolDetailVO);
    }

    /**
     * 根据输入的成绩查看可能录取的学校
     * @param score
     * @param page
     * @param size
     * @param provinceId
     * @return
     */
    @Override
    public Result<IPage<SchoolSimpleVO>> queryByScore(Integer score, int page, int size, Long provinceId) {
        //根据省份的ID查询该省所有学校的今年分数线,score+10>=school_score_this_year&&score-10<=school_score_this_year
        List<ProvinceInfo> provinceInfoList = provinceInfoMapper.selectList(new QueryWrapper<ProvinceInfo>()
                .eq("province_id", provinceId)
                .le("school_score_this_year", score + 10)
                .ge("school_score_this_year", score - 10));
        List<Long> schoolIds=new ArrayList<>();
        for (ProvinceInfo provinceInfo : provinceInfoList) {
            Long schoolId = provinceInfo.getSchoolId();
            schoolIds.add(schoolId);
        }
        List<SchoolInfo> schoolInfos = schoolInfoMapper.selectBatchIds(schoolIds);
        schoolInfos=schoolInfos.stream().sorted(Comparator.comparingInt(SchoolInfo::getSchoolRank)).collect(Collectors.toList());
        IPage<SchoolInfo> schoolInfoIPage = MpListPageUtil.getPage(schoolInfos, page, size);
        IPage<SchoolSimpleVO> simpleVOIPage = schoolInfoIPage.convert(schoolInfo -> {
            SchoolSimpleVO schoolSimpleVO = new SchoolSimpleVO();
            BeanUtils.copyProperties(schoolInfo, schoolSimpleVO);
            return schoolSimpleVO;
        });
        return Result.ok(simpleVOIPage);
    }
}
