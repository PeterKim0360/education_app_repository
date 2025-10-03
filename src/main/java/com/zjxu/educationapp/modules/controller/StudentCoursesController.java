package com.zjxu.educationapp.modules.controller;

import com.zjxu.educationapp.common.utils.Result;
import com.zjxu.educationapp.modules.entity.SemesterConfig;
import com.zjxu.educationapp.modules.service.SemesterConfigService;
import com.zjxu.educationapp.modules.service.StudentCoursesService;
import com.zjxu.educationapp.modules.vo.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 *学生课程相关接口
 */
@Tag(name = "学生课程相关接口")
@Slf4j
@RestController
@RequestMapping("/api/student/courses")
public class StudentCoursesController {
   @Autowired
   private StudentCoursesService studentCoursesService;
   @Autowired
   private SemesterConfigService semesterConfigService;

    /**
     * 查看所有课程
     */
    @Operation(summary = "查看所有课程")
    @GetMapping("/list")
    public Result<List<StudentSubjectsVO>> querySubjects() {
        log.info("用户请求查看所有课程");
        return studentCoursesService.querySubjects();
    }


    /**
     * 获取课程详情
     */
    @Operation(summary = "获取课程详情",description = "传参：subjectId")
    @GetMapping("/detail")
    public Result<StuSubjectDetailVO> queryDetail(@RequestParam("subjectId") Integer subjectId) {
        log.info("用户请求获取课程详情");
        return studentCoursesService.queryDetail(subjectId);
    }

    /**
     * 查看随堂作业（只展示刚发布未截止的）
     */
    @Operation(summary = "查看随堂作业",description = "传参：subjectId")
    @GetMapping("/work")
    public Result<List<HomeworkInClassStuVO>> queryWork(@RequestParam("subjectId") Integer subjectId) {
        log.info("用户请求查看随堂作业");
        return studentCoursesService.queryWork(subjectId);
    }

    /**
     * 查看已过期的随堂作业
     */
    @Operation(summary = "查看已过期的随堂作业",description = "传参：subjectId")
    @GetMapping("/expire/work")
    public Result<List<HomeworkInClassStuVO>> queryExpireWork(@RequestParam("subjectId") Integer subjectId) {
        log.info("用户请求查看已过期的随堂作业");
        return studentCoursesService.queryExpireWork(subjectId);
    }

    /**
     * 获取历史课件
     */
    @Operation(summary = "获取历史课件",description = "传参：subjectId")
    @GetMapping("/file/list")
    public Result<List<CourseHistoryVO>> queryFileList(@RequestParam("subjectId") Integer subjectId) {
        log.info("用户请求获取历史课件");
        return studentCoursesService.queryFileList(subjectId);
    }

//    /**
//     * 查看学期并选择
//     */
//    @Operation(summary = "查看所有学期")
//    @GetMapping("/semester")
//    public Result<List<SemesterVO>> querySemester() {
//        log.info("用户请求查看学期");
//        List<SemesterConfig> semesterConfig = semesterConfigService.list();
//        List<SemesterVO> semesterVOList = semesterConfig.stream().map(semesterConfig1 -> {
//            SemesterVO semesterVO = new SemesterVO();
//            BeanUtils.copyProperties(semesterConfig1, semesterVO);
//            return semesterVO;
//        }).toList();
//        return Result.ok(semesterVOList);
//    }
//
//    /**
//     * 切换学期
//     */
//    @Operation(summary = "切换学期",description = "传参：semesterId")
//    @PostMapping("/semester/change")
//    public Result<Long> changeSemester(@RequestParam("semesterId") Long semesterId) {
//        log.info("用户请求切换学期");
//        return studentCoursesService.changeSemester(semesterId);
//    }

    /**
     * 综合查询课表
     */
    @Operation(summary = "查询课表",description = "传参：week, 可选: weekday")
    @GetMapping("/combined")
    Result<List<ScheduleDetailVO>> queryCombinedSimple(
            @RequestParam(value = "week") String week ,
            @RequestParam(value = "weekday",required = false) String weekday){
        return studentCoursesService.queryCombinedSimple(week,weekday);
    }

//    /**
//     * 查询课表（详细）
//     */
//    @Operation(summary = "查询课表（详细）",description = "传参：courseId,semesterId")
//    @GetMapping("/schedule/detail")
//    public Result<ScheduleDetailVO> queryScheduleDetail(@RequestParam("courseId") Integer courseId,
//                                                              @RequestParam(value = "semesterId") Long semesterId){
//        log.info("用户请求查询课表（详细）");
//        return studentCoursesService.queryScheduleDetail(courseId,semesterId);
//    }

    

//     /**
//     * 查看选课信息
//     */
//    @Operation(summary = "查看选课信息")
//    @GetMapping("/select/list")

//    /**
//     * 学生选课
//     */
//    @Operation(summary = "学生选课",description = "传参：subjectId")
//    @PostMapping("/select")
//    public Result<?> select(@RequestParam("subjectId") Integer subjectId,
//                            @RequestParam("classId") Long classId) {
//        log.info("用户请求选课");
//        return studentCoursesService.select(subjectId,classId);
//    }
//
//    /**
//     * 学生退课
//     */
//    @Operation(summary = "学生退课",description = "传参：subjectId")
//    @PostMapping("/cancel")
//    public Result<?> cancel(@RequestParam("subjectId") Integer subjectId,
//                            @RequestParam("classId") Long classId) {
//        log.info("用户请求退课");
//        return studentCoursesService.cancel(subjectId,classId);
//
//    }

//    /**
//     * 查看即将上课的课程
//     */
//    @Operation(summary = "查看即将上课的课程")
//    @GetMapping("/upcoming")
//    public Result<List<StudentSubjectsVO>> queryUpcoming() {
//        log.info("用户请求查看即将上课的课程");
//        return studentCoursesService.queryUpcoming();
//    }

}
