package com.zjxu.educationapp.modules.controller;

import com.zjxu.educationapp.common.utils.Result;
import com.zjxu.educationapp.modules.service.StudentCoursesService;
import com.zjxu.educationapp.modules.vo.StudentSubjectsVO;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
     * 查看即将上课的课程
     */

    /**
     * 获取课程详情
     */
}
