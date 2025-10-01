package com.zjxu.educationapp.modules.service;

import com.zjxu.educationapp.common.utils.Result;
import com.zjxu.educationapp.modules.vo.CourseHistoryVO;
import com.zjxu.educationapp.modules.vo.HomeworkInClassStuVO;
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

    /**
     * 查询随堂作业（只展示刚发布未截止的）
     *
     * @param subjectId
     * @return
     */
    Result<List<HomeworkInClassStuVO>> queryWork(Integer subjectId);

    /**
     * 查询已截止的随堂作业
     *
     * @param subjectId
     * @return
     */
    Result<List<HomeworkInClassStuVO>> queryExpireWork(Integer subjectId);

    /**
     * 获取历史课件
     *
     * @param subjectId
     * @return
     */
    Result<List<CourseHistoryVO>> queryFileList(Integer subjectId);

//    /**
//     * 选课
//     *
//     * @param subjectId
//     * @param classId
//     * @return
//     */
//    Result<?> select(Integer subjectId, Long classId);
//
//    /**
//     * 取消选课
//     *
//     * @param subjectId
//     * @param classId
//     * @return
//     */
//    Result<?> cancel(Integer subjectId, Long classId);

}
