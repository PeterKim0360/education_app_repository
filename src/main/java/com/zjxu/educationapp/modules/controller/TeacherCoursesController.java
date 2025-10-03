package com.zjxu.educationapp.modules.controller;

import com.zjxu.educationapp.common.utils.Result;
import com.zjxu.educationapp.modules.dto.HomeworkInClassDTO;
import com.zjxu.educationapp.modules.dto.HomeworkInClassEditDTO;
import com.zjxu.educationapp.modules.dto.HomeworkInClassSendDTO;
import com.zjxu.educationapp.modules.dto.TeacherSendFileDTO;
import com.zjxu.educationapp.modules.service.TeacherCoursesService;
import com.zjxu.educationapp.modules.service.TeacherService;
import com.zjxu.educationapp.modules.vo.HomeworkInClassVO;
import com.zjxu.educationapp.modules.vo.TeacherClassVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 教师课程相关接口
 */
@Tag(name = "教师课程相关接口")
@RestController
@Slf4j
@RequestMapping("/api/teacher/courses")
public class TeacherCoursesController {
    @Autowired
    private TeacherCoursesService teacherCoursesService;

    /**
     * 上传课件
     */
    @Operation(summary = "上传课件",description = "传参：teacherSendFileDTO")
    @PostMapping("/uploadFile")
    public Result<?> uploadFile(@RequestBody TeacherSendFileDTO teacherSendFileDTO){
        log.info("上传课件:{}",teacherSendFileDTO);
        return teacherCoursesService.uploadFile(teacherSendFileDTO);
    }
    /**
     * 开始上课
     */
    @Operation(summary = "开始上课",description = "传参：subjectId")
    @PostMapping("/start/class")
    public Result<?> startClass(@RequestParam("subjectId") Integer subjectId) {
        log.info("开始上课");
        return teacherCoursesService.startClass(subjectId);
    }

    /**
     * 结束上课
     */
    @Operation(summary = "结束上课",description = "传参：subjectId")
    @PostMapping("/end/class")
    public Result<?> endClass(@RequestParam("subjectId") Integer subjectId) {
        log.info("结束上课");
        return teacherCoursesService.endClass(subjectId);
    }

    /**
     * 创建随堂作业
     */
    @Operation(summary = "创建随堂作业",description = "传参：homeworkInClassDTO")
    @PostMapping("/create/work")
    public Result<?> createWork(@RequestBody HomeworkInClassDTO homeworkInClassDTO) {
        log.info("创建随堂作业:{}",homeworkInClassDTO);
        return teacherCoursesService.createWork(homeworkInClassDTO);
    }

    /**
     * 编辑随堂作业
     */
    @Operation(summary = "编辑随堂作业",description = "传参：homeworkInClassEditDTO")
    @PostMapping("/edit/work")
    public Result<?> editWork(@RequestBody HomeworkInClassEditDTO homeworkInClassEditDTO) {
        log.info("编辑随堂作业:{}",homeworkInClassEditDTO);
        return teacherCoursesService.editWork(homeworkInClassEditDTO);
    }

    /**
     * 查看随堂作业
     */
    @Operation(summary = "查看随堂作业",description = "传参：subjectId")
    @GetMapping("/work")
    public Result<List<HomeworkInClassVO>> work(@RequestParam("subjectId") Integer subjectId) {
        log.info("查看随堂作业");
        return teacherCoursesService.work(subjectId);
    }

    /**
     * 发布随堂作业
     */
    @Operation(summary = "发布随堂作业",description = "传参：homeworkInClassSendDTO")
    @PostMapping("/send/work")
    public Result<?> sendWork(@RequestBody HomeworkInClassSendDTO homeworkInClassSendDTO) {
        log.info("发布随堂作业:{}",homeworkInClassSendDTO);
        return teacherCoursesService.sendWork(homeworkInClassSendDTO);
    }
}
