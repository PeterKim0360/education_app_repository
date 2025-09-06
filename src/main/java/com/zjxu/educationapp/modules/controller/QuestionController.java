package com.zjxu.educationapp.modules.controller;

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
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

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
    private SingleChoiceService singleChoiceService;
    @Autowired
    private MultipleChoiceService multipleChoiceService;
    @Autowired
    private TrueFalseService trueFalseService;
    @Autowired
    private FillInBlankService fillInBlankService;
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
     * 错题分页查询
     */
    @Operation(summary = "错题分页查询",description = "传参：subjectId;可选：page,size")
    @GetMapping("/error")
    public Result<IPage<ErrorQuestionsVO>> queryErrorQuestions(
            @RequestParam("subjectId") int subjectId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "8") int size){
        log.info("根据学科ID:{}，查询未掌握的错题",subjectId);
        List<ErrorQuestions> errorQuestions = errorQuestionsService.
                list(new QueryWrapper<ErrorQuestions>()
                        .eq("subject_id", subjectId)
                        .eq("is_mastered",0)
                        .orderByDesc("created_time"));
        List<ErrorQuestionsVO> errorQuestionsVOS=new ArrayList<>();

        for (ErrorQuestions errorQuestion : errorQuestions) {
            ErrorQuestionsVO errorQuestionsVO = new ErrorQuestionsVO();
            //将ErrorQuestions复制给ErrorQusVO
            BeanUtils.copyProperties(errorQuestion,errorQuestionsVO);
            // 初始化空字段为""
            initEmptyFields(errorQuestionsVO);
            //清理题干空格
            errorQuestionsVO.setQuestionText(StrUtil.trim(errorQuestionsVO.getQuestionText()));
            //查该学科的单选错题
            SingleChoice singleChoice = singleChoiceService.getOne(new QueryWrapper<SingleChoice>().eq("question_id", errorQuestion.getQuestionId()));
            if (singleChoice!=null) {
                //将SingleChoice复制给ErrorQusVO
                errorQuestionsVO.setOptionA(StrUtil.trim(singleChoice.getOptionA()));
                errorQuestionsVO.setOptionB(StrUtil.trim(singleChoice.getOptionB()));
                errorQuestionsVO.setOptionC(StrUtil.trim(singleChoice.getOptionC()));
                errorQuestionsVO.setOptionD(StrUtil.trim(singleChoice.getOptionD()));

                errorQuestionsVO.setCorrectOption(StrUtil.trim(singleChoice.getCorrectOption()));
                errorQuestionsVO.setUserAnswer(StrUtil.trim(singleChoice.getUserAnswer()));

                errorQuestionsVOS.add(errorQuestionsVO);
                continue;
            }
            //查该学科的多选错题
            MultipleChoice multipleChoice = multipleChoiceService.getOne(new QueryWrapper<MultipleChoice>().eq("question_id", errorQuestion.getQuestionId()));
            if (multipleChoice!=null){
                //将MultipleChoice复制给ErrorQusVO
                String choices = StrUtil.trim(multipleChoice.getOptions());
                List<String> optionList=new ArrayList<>();
                if (!choices.isEmpty()&&choices.length()>0){
                    // 按换行符拆分（兼容Windows \r\n 和 Linux \n）
                    String[] optionArray = choices.split("\\r?\\n");
                    for (String option : optionArray) {
                        optionList.add(StrUtil.trim(option));
                    }
                }
                errorQuestionsVO.setOptions(optionList);
                errorQuestionsVO.setCorrectOption(StrUtil.trim(multipleChoice.getCorrectOptions()));
                errorQuestionsVO.setUserAnswer(StrUtil.trim(multipleChoice.getUserAnswer()));

                errorQuestionsVOS.add(errorQuestionsVO);
                continue;
            }
            //查该学科的判断错题
            TrueFalse trueFalse = trueFalseService.getOne(new QueryWrapper<TrueFalse>().eq("question_id", errorQuestion.getQuestionId()));
            if (trueFalse!=null){
                //将TrueFalse复制给ErrorQusVO
                errorQuestionsVO.setCorrectResult(trueFalse.getCorrectResult()==null?false:trueFalse.getCorrectResult());
                errorQuestionsVO.setTrueFalseUserAnswer(trueFalse.getTrueFalseUserAnswer()==null?false:trueFalse.getTrueFalseUserAnswer());

                errorQuestionsVOS.add(errorQuestionsVO);
                continue;
            }
            //查该学科的填空错题
            FillInBlank fillInBlank = fillInBlankService.getOne(new QueryWrapper<FillInBlank>().eq("question_id", errorQuestion.getQuestionId()));
            if (fillInBlank!=null) {
                //将FillInBlank复制给ErrorQusVO
                errorQuestionsVO.setCorrectOption(StrUtil.trim(fillInBlank.getCorrectAnswers()));
                errorQuestionsVO.setUserAnswer(StrUtil.trim(fillInBlank.getUserAnswers()));

                errorQuestionsVOS.add(errorQuestionsVO);
            }
        }
        log.info("{}",errorQuestionsVOS);
        IPage<ErrorQuestionsVO> questionsVOIPage = MpListPageUtil.getPage(errorQuestionsVOS, page, size);
        return Result.ok(questionsVOIPage);
    }

    //初始化VO的空字段为""（避免JSON中出现null）
    private void initEmptyFields(ErrorQuestionsVO vo) {
        vo.setOptionA(StrUtil.blankToDefault(vo.getOptionA(), ""));
        vo.setOptionB(StrUtil.blankToDefault(vo.getOptionB(), ""));
        vo.setOptionC(StrUtil.blankToDefault(vo.getOptionC(), ""));
        vo.setOptionD(StrUtil.blankToDefault(vo.getOptionD(), ""));
        vo.setOptions(new ArrayList<>());
        vo.setCorrectOption(StrUtil.blankToDefault(vo.getCorrectOption(), ""));
        vo.setUserAnswer(StrUtil.blankToDefault(vo.getUserAnswer(), ""));
        // 布尔类型默认值
        if (vo.getCorrectResult() == null) vo.setCorrectResult(false);
        if (vo.getTrueFalseUserAnswer() == null) vo.setTrueFalseUserAnswer(false);
    }

    /**
     *错题记载
     */
    @Operation(summary = "错题记载",description = "传参：errorQuestionDTO")
    @PostMapping("/error/insert")
    public Result ErrorQuestionInsert(@RequestBody ErrorQuestionDTO errorQuestionDTO){
        //TODO
        return Result.ok();
    }

}