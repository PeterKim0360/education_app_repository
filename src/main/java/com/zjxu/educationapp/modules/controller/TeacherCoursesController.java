package com.zjxu.educationapp.modules.controller;

import com.zjxu.educationapp.common.utils.Result;
import com.zjxu.educationapp.modules.dto.TeacherSendFileDTO;
import com.zjxu.educationapp.modules.service.TeacherCoursesService;
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
     * 获取老师上课对应的所有班级
     */
    @Operation(summary = "获取老师上课对应的所有班级",description = "传参：subjectId")
    @GetMapping("/getClazz")
    public Result<List<TeacherClassVO>> getClazz(@RequestParam("subjectId") Integer subjectId){
        log.info("获取老师上课对应所有班级:{}",subjectId);
        return teacherCoursesService.getClazz(subjectId);
    }

}
