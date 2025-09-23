package com.zjxu.educationapp.modules.controller;

import com.zjxu.educationapp.common.utils.Result;
import com.zjxu.educationapp.modules.service.StudentLearningPlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * AI 助理学习规划相关接口
 */
@RestController
@Tag(name = "AI 助理学习规划相关接口")
@RequestMapping("/api/learning-plan")
@Slf4j
public class LearningPlanController {

    @Autowired
    private StudentLearningPlanService studentLearningPlanService;

    /**
     * 获取学习计划
     */
    @GetMapping("/generate")
    @PostMapping("/generate")
    public Result<?> getLearningPlan() {
        log.info("获取学习计划");
        return studentLearningPlanService.getLearningPlan();
    }
}
