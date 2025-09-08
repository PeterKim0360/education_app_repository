package com.zjxu.educationapp.modules.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjxu.educationapp.common.utils.MpListPageUtil;
import com.zjxu.educationapp.common.utils.Result;
import com.zjxu.educationapp.modules.dto.ErrorQuestionDTO;
import com.zjxu.educationapp.modules.entity.*;
import com.zjxu.educationapp.modules.mapper.*;
import com.zjxu.educationapp.modules.service.ErrorQuestionsService;
import com.zjxu.educationapp.modules.vo.ErrorQuestionsVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
* @author huawei
* @description 针对表【error_questions】的数据库操作Service实现
* @createDate 2025-09-07 17:19:04
*/
@Slf4j
@Service
public class ErrorQuestionsServiceImpl extends ServiceImpl<ErrorQuestionsMapper, ErrorQuestions>
    implements ErrorQuestionsService{
    @Autowired
    private ErrorQuestionsMapper errorQuestionsMapper;
    @Autowired
    private SingleChoiceMapper singleChoiceMapper;
    @Autowired
    private MultipleChoiceMapper multipleChoiceMapper;
    @Autowired
    private TrueFalseMapper trueFalseMapper;
    @Autowired
    private FillInBlankMapper fillInBlankMapper;
    /**
     * 错题分页查询
     * @param subjectId
     * @param page
     * @param size
     * @return
     */
    @Override
    public Result<IPage<ErrorQuestionsVO>> queryErrorQuestions(int subjectId, int page, int size) {
        long userId = StpUtil.getLoginIdAsLong();
        List<ErrorQuestions> errorQuestions = errorQuestionsMapper.
                selectList(new QueryWrapper<ErrorQuestions>()
                        .eq("subject_id", subjectId)
                        .eq("is_mastered",0)     //0为未掌握
                        .eq("user_id",userId)
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
            SingleChoice singleChoice = singleChoiceMapper.selectOne(new QueryWrapper<SingleChoice>().eq("question_id", errorQuestion.getQuestionId()));
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
            MultipleChoice multipleChoice = multipleChoiceMapper.selectOne(new QueryWrapper<MultipleChoice>().eq("question_id", errorQuestion.getQuestionId()));
            if (multipleChoice!=null){
                //将MultipleChoice复制给ErrorQusVO
                String choices = StrUtil.trim(multipleChoice.getOptions());
                List<String> optionList=new ArrayList<>();
                if (!choices.isEmpty()&&choices.length()>0){
                    // 按","拆分
                    String[] optionArray = choices.split(",");
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
            TrueFalse trueFalse = trueFalseMapper.selectOne(new QueryWrapper<TrueFalse>().eq("question_id", errorQuestion.getQuestionId()));
            if (trueFalse!=null){
                //将TrueFalse复制给ErrorQusVO
                errorQuestionsVO.setCorrectResult(trueFalse.getCorrectResult()==null?false:trueFalse.getCorrectResult());
                errorQuestionsVO.setTrueFalseUserAnswer(trueFalse.getTrueFalseUserAnswer()==null?false:trueFalse.getTrueFalseUserAnswer());

                errorQuestionsVOS.add(errorQuestionsVO);
                continue;
            }
            //查该学科的填空错题
            FillInBlank fillInBlank = fillInBlankMapper.selectOne(new QueryWrapper<FillInBlank>().eq("question_id", errorQuestion.getQuestionId()));
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

    /**
     * 错题记载
     * @param errorQuestionDTO
     * @return
     */
    @Override
    public Result<?> insertQuestions(ErrorQuestionDTO errorQuestionDTO) {
        //题型类型code=1->单选，code=2->多选，code=3->判断，code=4->填空
        long userId = StpUtil.getLoginIdAsLong();
        Integer code = errorQuestionDTO.getCode();
        ErrorQuestions questions = new ErrorQuestions();
        log.info("题干：{}",errorQuestionDTO.getQuestionText());
        questions.setQuestionText(StrUtil.trim(errorQuestionDTO.getQuestionText()));
        questions.setCreatedTime(new Date());
        questions.setIsMastered(false);
        questions.setSubjectId(errorQuestionDTO.getSubjectId());
        questions.setUserId(userId);
        //将questions保存到error_questions数据库
        errorQuestionsMapper.insert(questions);
        Integer questionId = questions.getQuestionId();
        if (code==1){
            //为单选题
            SingleChoice singleChoice = SingleChoice.builder()
                    .questionId(questionId)
                    .optionA(StrUtil.trim(errorQuestionDTO.getOptionA()))
                    .optionB(StrUtil.trim(errorQuestionDTO.getOptionB()))
                    .optionC(StrUtil.trim(errorQuestionDTO.getOptionC()))
                    .optionD(StrUtil.trim(errorQuestionDTO.getOptionD()))
                    .correctOption(errorQuestionDTO.getSingleCorrectOption())
                    .userAnswer(errorQuestionDTO.getSingleUserAnswer())
                    .build();
            //保存到单选题的数据库
            singleChoiceMapper.insert(singleChoice);
        } else if (code == 2) {
            //为多选
            MultipleChoice multipleChoice = MultipleChoice.builder()
                    .questionId(questionId)
                    .options(StrUtil.join(",",errorQuestionDTO.getOptions()))
                    .correctOptions(StrUtil.trim(errorQuestionDTO.getMultipleCorrectOptions()))
                    .userAnswer(StrUtil.trim(errorQuestionDTO.getMultipleUserAnswer()))
                    .build();
            //保存到多选题的数据库
            multipleChoiceMapper.insert(multipleChoice);
        } else if (code == 3) {
            //为判断
            TrueFalse trueFalse = TrueFalse.builder()
                    .questionId(questionId)
                    .correctResult(errorQuestionDTO.getTrueFalseCorrectResult())
                    .TrueFalseUserAnswer(errorQuestionDTO.getTrueFalseUserAnswer())
                    .build();
            //保存到判断题的数据库
            trueFalseMapper.insert(trueFalse);
        } else{
            //填空
            FillInBlank fillInBlank = FillInBlank.builder()
                    .questionId(questionId)
                    .correctAnswers(errorQuestionDTO.getFillInBlankCorrectAnswers())
                    .userAnswers(errorQuestionDTO.getFillInBlankUserAnswers())
                    .build();
            //保存到填空题的数据库
            fillInBlankMapper.insert(fillInBlank);
        }
        return Result.ok();
    }

    /**
     * 初始化VO的空字段为""（避免JSON中出现null）
     * @param vo
     */
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
}




