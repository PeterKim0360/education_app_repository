package com.zjxu.educationapp.modules.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zjxu.educationapp.common.utils.Result;
import com.zjxu.educationapp.modules.entity.SchoolInfo;
import com.zjxu.educationapp.modules.vo.SchoolDetailVO;
import com.zjxu.educationapp.modules.vo.SchoolSimpleVO;

public interface SchoolService extends IService<SchoolInfo> {

    /**
     *学校分页查询（含模糊）
     * @return
     */
    Result<IPage<SchoolSimpleVO>> querySchoolInfoVO(String schoolName, Integer page, Integer size);

    /**
     * 根据学校id查详情
     * @param schoolId
     * @param provinceId
     * @return
     */
    Result<SchoolDetailVO> queryDetail(Long schoolId, Long provinceId);

    /**
     * 根据输入的成绩查看可能录取的学校
     * @param score
     * @param page
     * @param size
     * @param provinceId
     * @return
     */
    Result<IPage<SchoolSimpleVO>> queryByScore(Integer score, int page, int size, Long provinceId);
}
