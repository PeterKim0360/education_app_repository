package com.zjxu.educationapp.modules.service;

import com.zjxu.educationapp.common.utils.Result;
import com.zjxu.educationapp.modules.vo.*;

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
    Result<List<StuSubjectDetailVO>> queryDetail(Integer subjectId);

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


    Result<List<CourseHistoryVO>> queryFileList(Integer subjectId);

//    Result<Long> changeSemester(Long semesterId);


//    /**
//     * 查询课表（详细）
//     *
//     * @param courseId
//     * @param semesterId
//     * @return
//     */
//    Result<ScheduleDetailVO> queryScheduleDetail(Integer courseId, Long semesterId);

    /**
     * 查询课表
     * @param week
     * @param weekday
     * @return
     */
    Result<List<ScheduleDetailVO>> queryCombinedSimple(String week, String weekday);

//    /**
//     * 获取课表（一周内）
//     * @param week
//     * @return
//     */
//    Result<List<ScheduleDetailVO>> queryScheduleWeek(String week);


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
