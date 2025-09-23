package com.zjxu.educationapp.modules.service;

import com.zjxu.educationapp.common.utils.Result;
import com.zjxu.educationapp.modules.vo.StuSubjectDetailVO;
import com.zjxu.educationapp.modules.vo.StudentSubjectsVO;

import java.util.List;

public interface StudentCoursesService {
    /**
     * 查询所有课程
     * @return
     */
    Result<List<StudentSubjectsVO>> querySubjects();

    /**
     * 查询课程详情
     *
     * @param subjectId
     * @return
     */
    Result<StuSubjectDetailVO> queryDetail(Integer subjectId);
}
