package com.zjxu.educationapp.modules.controller;

import com.zjxu.educationapp.common.utils.Result;
import com.zjxu.educationapp.modules.service.ClassService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 班级管理
 */
@RestController
@Slf4j
@Tag(name = "班级管理相关接口")
@RequestMapping("/api/class")
public class ClassController {
    @Autowired
    private ClassService classService;

    /**
     * 创建班级
     */
    @Operation(summary = "创建班级",description = "传参：className")
    @PostMapping("/create")
    public Result<?> createClass(String className,Integer subjectId) {
        log.info("创建班级:{},对应学科:{}",className,subjectId);
        return classService.createClass(className,subjectId);
    }

    /**
     * 添加学生
     */
}
