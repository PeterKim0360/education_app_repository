package com.zjxu.educationapp.modules.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zjxu.educationapp.common.utils.Result;
import com.zjxu.educationapp.modules.dto.ErrorQuestionDTO;
import com.zjxu.educationapp.modules.entity.*;
import com.zjxu.educationapp.modules.service.*;
import com.zjxu.educationapp.modules.service.impl.QuestionService;
import com.zjxu.educationapp.modules.vo.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

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
            @RequestParam(defaultValue = "10") int totalCount,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "5") int pageSize) throws Exception {
        log.info("生成任意主题、题型的题目");
        QuestionResult result = questionService.generateQuestions(
                questionType, questionStyle,totalCount, pageNum, pageSize);
        return ResponseEntity.ok(result);
    }

    /**
     * AI 生成该学科错题报告
     */
    @Operation(summary = "AI 生成该学科错题报告",description = "传参：subjectId")
    @GetMapping("/summary")
    public Result<QuestionResultSummary> generateSummary(
            @RequestParam("subjectId") int subjectId,
            @RequestParam("questionIds") List<Integer> questionIds){
        log.info("AI 获取该学科错题总结");
        return questionService.summary(subjectId,questionIds);
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

    /**
     * 查看单选题
     */
    @Operation(summary = "查看单选题",description = "传参：subjectId;可选：page,size")
    @GetMapping("/singleChoice")
    public Result<IPage<SingleChoiceVO>> querySingleChoice(
            @RequestParam("subjectId") int subjectId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size){
        log.info("查看单选题");
        return errorQuestionsService.querySingleChoice(subjectId,page,size);
    }

    /**
     * 查看多选题
     */
    @Operation(summary = "查看多选题",description = "传参：subjectId;可选：page,size")
    @GetMapping("/multipleChoice")
    public Result<IPage<MultipleChoiceVO>> queryMultipleChoice(@RequestParam("subjectId") Integer subjectId,
                                                               @RequestParam(defaultValue = "1") int page,
                                                               @RequestParam(defaultValue = "5") int size){
        log.info("查看多选题");
        return errorQuestionsService.queryMultipleChoice(subjectId,page,size);
    }

    /**
     * 查看判断题
     */
    @Operation(summary = "查看判断题",description = "传参：subjectId;可选：page,size")
    @GetMapping("/trueFalse")
    public Result<IPage<TrueFalseVO>> queryTrueFalse(@RequestParam("subjectId") Integer subjectId,
                                                     @RequestParam(defaultValue = "1") int page,
                                                     @RequestParam(defaultValue = "5") int size){
        log.info("查看判断题");
        return errorQuestionsService.queryTrueFalse(subjectId,page,size);
    }

    /**
     * 查看填空题
     */
    @Operation(summary = "查看填空题",description = "传参：subjectId;可选：page,size")
    @GetMapping("/fillInBlank")
    public Result<IPage<FillInBlankVO>> queryFillInBlank(@RequestParam("subjectId") Integer subjectId,
                                                         @RequestParam(defaultValue = "1") int page,
                                                         @RequestParam(defaultValue = "5") int size){
        log.info("查看填空题");
        return errorQuestionsService.queryFillInBlank(subjectId,page,size);
    }

//    /**
//     * 开始对应学科错题循环练习
//     */
//    @Operation(summary = "开始对应学科错题循环练习")
//    @PostMapping("/practice/start")
//    public Result<Map<Long,List<ErrorQuestionsVO>>> startPractice(
//            @RequestParam Integer subjectId,
//            @RequestParam(defaultValue = "10") int questionCount) {
//        Long studentId = StpUtil.getLoginIdAsLong();
//        return errorQuestionsService.initPractice(studentId, subjectId, questionCount);
//    }

    /**
     * 提交答案
     */
    @Operation(summary = "提交答案")
    @PostMapping("/practice/submit")
    public Result<PracticeNextVO> submitAnswer(
            @RequestParam Long sessionId,
            @RequestParam Integer questionId,
            @RequestParam boolean isCorrect) {
        return errorQuestionsService.submitAnswer(sessionId, questionId, isCorrect);
    }
//
//    /**
//     * 获取当前练习状态（用于恢复）
//     */
//    @Operation(summary = "获取练习状态")
//    @GetMapping("/practice/state")
//    public Result<PracticeStateVO> getPracticeState(@RequestParam Long sessionId) {
//        return errorQuestionsService.getPracticeState(sessionId);
//    }

    /**
     * 恢复或开始练习
     */
    @Operation(summary = "恢复或开始练习")
    @PostMapping("/practice/resume")
    public Result<PracticeStateVO> resumeOrStart(@RequestParam Integer subjectId,
                                                 @RequestParam Integer questionType,
                                                 @RequestParam(defaultValue = "10") int questionCount) {
        Long studentId = StpUtil.getLoginIdAsLong();
        return errorQuestionsService.resumeOrStart(studentId, subjectId, questionCount,questionType);
    }

}