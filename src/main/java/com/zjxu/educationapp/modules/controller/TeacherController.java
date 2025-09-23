package com.zjxu.educationapp.modules.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zjxu.educationapp.common.utils.Result;
import com.zjxu.educationapp.modules.service.TeacherService;
import com.zjxu.educationapp.modules.vo.StudentSimpleVO;
import com.zjxu.educationapp.modules.vo.SubjectsVO;
import com.zjxu.educationapp.modules.vo.TeacherClassVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
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
    @Operation(summary = "获取老师对应学科的上课班级",description = "传参：subjectId")
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
    @Transactional
    public Result<?> createClass(@RequestParam("className") String className,@RequestParam("subjectId") Integer subjectId) {
        log.info("创建班级:{},对应学科:{}",className,subjectId);
        return teacherService.createClass(className,subjectId);
    }

    /**
     * 查看对应班级的学生
     */
    @Operation(summary = "查看对应班级的学生",description = "传参：classId;可选：page,size")
    @GetMapping("/stuList")
    public Result<IPage<StudentSimpleVO>> stuList(
            @RequestParam("classId") Long classId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "8")int size) {
        log.info("查看对应班级的学生:{}",classId);
        return teacherService.stuList(classId,page,size);
    }

    /**
     * 删除学生
     */
    @Operation(summary = "删除学生",description = "传参：stuIds")
    @DeleteMapping("/delete/stus")
    @Transactional
    public Result<?> deleteStus(@RequestParam("stuIds") List<Long> stuIds) {
        log.info("删除学生:{}",stuIds);
        return teacherService.deleteStus(stuIds);
    }

    /**
     * 添加学生
     */
    @Operation(summary = "添加学生",description = "传参：stuIds")
    @PostMapping("/add/stus")
    @Transactional
    public Result<?> addStus(@RequestParam("stuIds") List<Long> stuIds,
                             @RequestParam("classId") Long classId) {
        log.info("添加学生:{}",stuIds);
        return teacherService.addStus(stuIds,classId);
    }

    /**
     * 开始上课
     */
    @Operation(summary = "开始上课",description = "传参：subjectId")
    @PostMapping("/start/class")
    public Result<?> startClass(@RequestParam("subjectId") Integer subjectId) {
        log.info("开始上课");
        return teacherService.startClass(subjectId);
    }

    /**
     * 结束上课
     */
    @Operation(summary = "结束上课",description = "传参：subjectId")
    @PostMapping("/end/class")
    public Result<?> endClass(@RequestParam("subjectId") Integer subjectId) {
        log.info("结束上课");
        return teacherService.endClass(subjectId);
    }
}
