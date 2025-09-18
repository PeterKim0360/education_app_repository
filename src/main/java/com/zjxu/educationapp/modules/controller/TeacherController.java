package com.zjxu.educationapp.modules.controller;

import com.zjxu.educationapp.common.utils.Result;
import com.zjxu.educationapp.modules.entity.SubjectClassTeach;
import com.zjxu.educationapp.modules.mapper.SubjectClassTeachMapper;
import com.zjxu.educationapp.modules.service.SubjectClassTeachService;
import com.zjxu.educationapp.modules.service.TeacherService;
import com.zjxu.educationapp.modules.vo.SubjectsVO;
import com.zjxu.educationapp.modules.vo.TeacherClassVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teacher")
@Slf4j
@Tag(name = "老师相关接口")
public class TeacherController {
    @Autowired
    private TeacherService teacherService;
    /**
     * 获取老师对应的学科
     * @return
     */
    @Operation(summary = "获取老师对应的学科")
    @GetMapping("/subject")
    public Result<List<SubjectsVO>> getSubjects() {
        log.info("获取老师对应的学科");
        return teacherService.getSubjects();
    }
    /**
     * 获取老师对应学科的上课班级
     */
    @Operation(summary = "获取老师对应学科的上课班级")
    @GetMapping("/subject/class")
    public Result<List<TeacherClassVO>> getSubjectClass(@RequestParam("subjectId") Integer subjectId) {
        log.info("获取老师对应学科的上课班级,学科ID：{}",subjectId);
        return teacherService.getClazz(subjectId);
    }

    /**
     * 创建班级
     */
    @Operation(summary = "创建班级",description = "传参：className,subjectId")
    @PostMapping("/create/class")
    public Result<?> createClass(@RequestParam("className") String className,@RequestParam("subjectId") Integer subjectId) {
        log.info("创建班级:{},对应学科:{}",className,subjectId);
        return teacherService.createClass(className,subjectId);
    }

    /**
     *查看对应班级的学生
     */
//    @Operation(summary = "查看对应班级的学生",description = "传参：classId")
//    @GetMapping("/stuList")
//    public Result<IPage<>> stuList(@RequestParam("classId") Long classId) {
//        log.info("查看对应班级的学生:{}",classId);
//        return classService.stuList(classId);
//    }

}
