package com.zjxu.educationapp.modules.controller;

import com.zjxu.educationapp.common.utils.Result;
import com.zjxu.educationapp.modules.entity.CourseHistory;
import com.zjxu.educationapp.modules.mapper.CourseHistoryMapper;
import com.zjxu.educationapp.modules.service.StudentCoursesService;
import com.zjxu.educationapp.modules.vo.CourseHistoryVO;
import com.zjxu.educationapp.modules.vo.HomeworkInClassStuVO;
import com.zjxu.educationapp.modules.vo.StuSubjectDetailVO;
import com.zjxu.educationapp.modules.vo.StudentSubjectsVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
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
