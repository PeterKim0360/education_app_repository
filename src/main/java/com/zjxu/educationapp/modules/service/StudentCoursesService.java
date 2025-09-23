package com.zjxu.educationapp.modules.service;

import com.zjxu.educationapp.common.utils.Result;
import com.zjxu.educationapp.modules.vo.StudentSubjectsVO;

import java.util.List;

public interface StudentCoursesService {
    /**
     * 查询所有课程
     * @return
     */
    Result<List<StudentSubjectsVO>> querySubjects();
}
