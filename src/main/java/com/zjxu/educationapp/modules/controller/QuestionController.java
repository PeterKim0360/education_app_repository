package com.zjxu.educationapp.modules.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zjxu.educationapp.common.utils.MpListPageUtil;
import com.zjxu.educationapp.common.utils.Result;
import com.zjxu.educationapp.modules.dto.ErrorQuestionDTO;
import com.zjxu.educationapp.modules.entity.*;
import com.zjxu.educationapp.modules.service.*;
import com.zjxu.educationapp.modules.service.impl.QuestionService;
import com.zjxu.educationapp.modules.vo.ErrorQuestionsVO;
import com.zjxu.educationapp.modules.vo.QuestionResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 错题接口
 */
@Tag(name = "错题接口")
@Slf4j
@RestController
@RequestMapping("/api/questions")
public class QuestionController {
    @Autowired
    private ErrorQuestionsService errorQuestionsService;
    @Autowired
    private SubjectsService subjectsService;
    private final QuestionService questionService;

    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    /**
     * 生成任意主题、题型的题目
      */
    @Operation(summary = "生成任意主题、题型的题目",description = "传参：questionType,questionStyle;可选：totalCount,pageNum,pageSize")
    @GetMapping("/generate")
    public ResponseEntity<QuestionResult> generateQuestions(
            @RequestParam String questionType,
            @RequestParam String questionStyle,
            @RequestParam(defaultValue = "10") int totalCount, //用户动态传入
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "5") int pageSize) throws Exception {

        QuestionResult result = questionService.generateQuestions(
                questionType, questionStyle,totalCount, pageNum, pageSize);
        return ResponseEntity.ok(result);
    }
    /**
     * 错题默认页面响应
     */
    @Operation(summary = "错题默认页面响应")
    @GetMapping
    public Result<IPage<Map<String, Map<Integer, String>>>> responseTypes(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size){
        return subjectsService.responseDefault(page,size);
    }

    /**
     * 错题分页查询
     */
    @Operation(summary = "错题分页查询",description = "传参：subjectId;可选：page,size")
    @GetMapping("/error")
    public Result<IPage<ErrorQuestionsVO>> queryErrorQuestions(
            @RequestParam("subjectId") int subjectId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "8") int size){
        log.info("根据学科ID:{}，查询未掌握的错题",subjectId);
        return errorQuestionsService.queryErrorQuestions(subjectId,page,size);
    }

    /**
     *错题记载
     */
    @Operation(summary = "错题记载",description = "传参：errorQuestionDTO")
    @PostMapping("/error/insert")
    @Transactional
    public Result<?> ErrorQuestionInsert(@RequestBody ErrorQuestionDTO errorQuestionDTO){
        return errorQuestionsService.insertQuestions(errorQuestionDTO);
    }

    /**
     *取消错题
     */
    @Operation(summary = "取消错题")
    @DeleteMapping("/error/delete")
    @Transactional
    public Result<?> ErrorQuestionDel(@RequestParam("questionId") int questionId){
        log.info("取消错题收藏");
        return errorQuestionsService.ErrorQuestionDel(questionId);
    }

    /**
     * AI生题提供的科目类型
     */
    @Operation(summary = "AI生题提供的科目类型")
    @GetMapping("/AI/questions/subject")
    public Result<IPage<Map<Integer,String>>> queryAIQuestions(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size){
        return subjectsService.queryAIQuestions(page,size);
    }

    /**
     * AI生题提供的题目类型
     */
    @Operation(summary = "AI生题提供的题目类型")
    @GetMapping("/AI/questions")
    public Result<List<String>> queryAIType(){
        return subjectsService.queryAIType();
    }

}