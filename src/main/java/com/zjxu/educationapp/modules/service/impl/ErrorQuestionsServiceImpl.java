package com.zjxu.educationapp.modules.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjxu.educationapp.common.utils.MpListPageUtil;
import com.zjxu.educationapp.common.utils.Result;
import com.zjxu.educationapp.modules.dto.ErrorQuestionDTO;
import com.zjxu.educationapp.modules.entity.*;
import com.zjxu.educationapp.modules.mapper.*;
import com.zjxu.educationapp.modules.service.ErrorQuestionsService;
import com.zjxu.educationapp.modules.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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
    @Autowired
    private PracticeSessionMapper practiceSessionMapper;
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
                        .eq("is_mastered",0)
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
                List<String> optionList = parseOptions(choices);
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
                String choices = StrUtil.trim(trueFalse.getOptions());
                List<String> optionList = parseOptions(choices);
                errorQuestionsVO.setOptions(optionList);
                errorQuestionsVO.setCorrectOption(StrUtil.trim(trueFalse.getCorrectResult()));
                errorQuestionsVO.setUserAnswer(StrUtil.trim(trueFalse.getUserAnswer()));
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
                    .subjectId(errorQuestionDTO.getSubjectId())
                    .userId(userId)
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
                    .subjectId(errorQuestionDTO.getSubjectId())
                    .userId(userId)
                    .build();
            //保存到多选题的数据库
            multipleChoiceMapper.insert(multipleChoice);
        } else if (code == 3) {
            //为判断
            TrueFalse trueFalse = TrueFalse.builder()
                    .questionId(questionId)
                    .correctResult(errorQuestionDTO.getTrueFalseCorrectResult())
                    .userAnswer(errorQuestionDTO.getTrueFalseUserAnswer())
                    .options(StrUtil.join(",",errorQuestionDTO.getOptions()))
                    .subjectId(errorQuestionDTO.getSubjectId())
                    .userId(userId)
                    .build();
            //保存到判断题的数据库
            trueFalseMapper.insert(trueFalse);
        } else{
            //填空
            FillInBlank fillInBlank = FillInBlank.builder()
                    .questionId(questionId)
                    .correctAnswers(errorQuestionDTO.getFillInBlankCorrectAnswers())
                    .userAnswers(errorQuestionDTO.getFillInBlankUserAnswers())
                    .subjectId(errorQuestionDTO.getSubjectId())
                    .userId(userId)
                    .build();
            //保存到填空题的数据库
            fillInBlankMapper.insert(fillInBlank);
        }
        return Result.ok();
    }

    /**
     * 取消错题
     * @param questionId
     * @return
     */
    @Override
    public Result<?> ErrorQuestionDel(int questionId) {
        long userId = StpUtil.getLoginIdAsLong();
        ErrorQuestions question = errorQuestionsMapper.selectOne(new QueryWrapper<ErrorQuestions>()
                .eq("user_id", userId)
                .eq("question_id", questionId));
        question.setIsMastered(true);
        errorQuestionsMapper.update(question,new QueryWrapper<ErrorQuestions>()
                .eq("question_id",questionId)
                .eq("user_id",userId));
        return Result.ok();
    }

    /**
     * 查询单选题
     * @param subjectId
     * @param page
     * @param size
     * @return
     */
    @Override
    public Result<IPage<SingleChoiceVO>> querySingleChoice(int subjectId, int page, int size) {
        List<SingleChoice> singleChoices = singleChoiceMapper.selectList(new LambdaQueryWrapper<SingleChoice>()
                .eq(SingleChoice::getSubjectId, subjectId)
                .eq(SingleChoice::getUserId,StpUtil.getLoginIdAsLong()));
        List<SingleChoiceVO> singleChoiceVOList= new ArrayList<>();
        for (SingleChoice singleChoice : singleChoices) {
            //根据题目ID获取错题目
            ErrorQuestions question = errorQuestionsMapper.selectById(singleChoice.getQuestionId());
            SingleChoiceVO singleChoiceVO = new SingleChoiceVO();
            singleChoiceVO.setQuestionId(singleChoice.getQuestionId());
            singleChoiceVO.setQuestionText(StrUtil.trim(question.getQuestionText()));
            singleChoiceVO.setIsMastered(question.getIsMastered());
            singleChoiceVO.setCreatedTime(question.getCreatedTime());
            singleChoiceVO.setSubjectId(singleChoice.getSubjectId());
            singleChoiceVO.setOptionA(StrUtil.trim(singleChoice.getOptionA()));
            singleChoiceVO.setOptionB(StrUtil.trim(singleChoice.getOptionB()));
            singleChoiceVO.setOptionC(StrUtil.trim(singleChoice.getOptionC()));
            singleChoiceVO.setOptionD(StrUtil.trim(singleChoice.getOptionD()));
            singleChoiceVO.setCorrectOption(StrUtil.trim(singleChoice.getCorrectOption()));
            singleChoiceVO.setUserAnswer(StrUtil.trim(singleChoice.getUserAnswer()));
            singleChoiceVOList.add(singleChoiceVO);
        }
        singleChoiceVOList.sort((a,b)->b.getCreatedTime().compareTo(a.getCreatedTime()));
        IPage<SingleChoiceVO> singleChoiceVOIPage = MpListPageUtil.getPage(singleChoiceVOList, page, size);
        return Result.ok(singleChoiceVOIPage);
    }

    /**
     * 查询多选题
     * @return
     */
    @Override
    public Result<IPage<MultipleChoiceVO>> queryMultipleChoice(Integer subjectId, int page, int size) {
        List<MultipleChoice> multipleChoices = multipleChoiceMapper.selectList(new LambdaQueryWrapper<MultipleChoice>()
                .eq(MultipleChoice::getSubjectId, subjectId)
                .eq(MultipleChoice::getUserId, StpUtil.getLoginIdAsLong()));
        List<MultipleChoiceVO> multipleChoiceVOList= new ArrayList<>();
        for (MultipleChoice multipleChoice : multipleChoices) {
            //根据题目ID获取错题目
            ErrorQuestions question = errorQuestionsMapper.selectById(multipleChoice.getQuestionId());
            MultipleChoiceVO multipleChoiceVO = new MultipleChoiceVO();
            multipleChoiceVO.setQuestionId(multipleChoice.getQuestionId());
            multipleChoiceVO.setQuestionText(StrUtil.trim(question.getQuestionText()));
            multipleChoiceVO.setIsMastered(question.getIsMastered());
            multipleChoiceVO.setCreatedTime(question.getCreatedTime());
            multipleChoiceVO.setSubjectId(multipleChoice.getSubjectId());

            String choices = StrUtil.trim(multipleChoice.getOptions());
            List<String> optionList = parseOptions(choices);
            multipleChoiceVO.setOptions(optionList);
            multipleChoiceVO.setCorrectOption(StrUtil.trim(multipleChoice.getCorrectOptions()));
            multipleChoiceVO.setUserAnswer(StrUtil.trim(multipleChoice.getUserAnswer()));
            multipleChoiceVOList.add(multipleChoiceVO);
        }
        multipleChoiceVOList.sort((a,b)->b.getCreatedTime().compareTo(a.getCreatedTime()));
        IPage<MultipleChoiceVO> multipleChoiceVOIPage = MpListPageUtil.getPage(multipleChoiceVOList, page, size);
        return Result.ok(multipleChoiceVOIPage);
    }

    /**
     * 获取判断题
     * @param subjectId
     * @param page
     * @param size
     * @return
     */
    @Override
    public Result<IPage<TrueFalseVO>> queryTrueFalse(Integer subjectId, int page, int size) {
        List<TrueFalse> trueFalses = trueFalseMapper.selectList(new LambdaQueryWrapper<TrueFalse>()
                .eq(TrueFalse::getSubjectId, subjectId)
                .eq(TrueFalse::getUserId, StpUtil.getLoginIdAsLong()));
        List<TrueFalseVO> trueFalseVOList= new ArrayList<>();
        for (TrueFalse trueFalse : trueFalses) {
            //根据题目ID获取错题目
            ErrorQuestions question = errorQuestionsMapper.selectById(trueFalse.getQuestionId());
            TrueFalseVO trueFalseVO = new TrueFalseVO();
            trueFalseVO.setQuestionId(trueFalse.getQuestionId());
            trueFalseVO.setQuestionText(StrUtil.trim(question.getQuestionText()));
            String choices = StrUtil.trim(trueFalse.getOptions());
            List<String> optionList = parseOptions(choices);
            trueFalseVO.setOptions(optionList);
            trueFalseVO.setIsMastered(question.getIsMastered());
            trueFalseVO.setCreatedTime(question.getCreatedTime());
            trueFalseVO.setSubjectId(trueFalse.getSubjectId());
            trueFalseVO.setCorrectResult(StrUtil.trim(trueFalse.getCorrectResult()));
            trueFalseVO.setTrueFalseUserAnswer(StrUtil.trim(trueFalse.getUserAnswer()));
            trueFalseVOList.add(trueFalseVO);
        }
        trueFalseVOList.sort((a,b)-> b.getCreatedTime().compareTo(a.getCreatedTime()));
        IPage<TrueFalseVO> trueFalseVOIPage = MpListPageUtil.getPage(trueFalseVOList, page, size);
        return Result.ok(trueFalseVOIPage);
    }

    /**
     * 获取填空题
     * @param subjectId
     * @param page
     * @param size
     * @return
     */
    @Override
    public Result<IPage<FillInBlankVO>> queryFillInBlank(Integer subjectId, int page, int size) {
        List<FillInBlank> fillInBlanks = fillInBlankMapper.selectList(new LambdaQueryWrapper<FillInBlank>()
                .eq(FillInBlank::getSubjectId, subjectId)
                .eq(FillInBlank::getUserId, StpUtil.getLoginIdAsLong()));
        List<FillInBlankVO> fillInBlankVOList= new ArrayList<>();
        for (FillInBlank fillInBlank : fillInBlanks) {
            //根据题目ID获取错题目
            ErrorQuestions question = errorQuestionsMapper.selectById(fillInBlank.getQuestionId());
            FillInBlankVO fillInBlankVO = new FillInBlankVO();
            fillInBlankVO.setQuestionId(fillInBlank.getQuestionId());
            fillInBlankVO.setQuestionText(StrUtil.trim(question.getQuestionText()));
            fillInBlankVO.setIsMastered(question.getIsMastered());
            fillInBlankVO.setCreatedTime(question.getCreatedTime());
            fillInBlankVO.setSubjectId(fillInBlank.getSubjectId());
            fillInBlankVO.setCorrectAnswer(StrUtil.trim(fillInBlank.getCorrectAnswers()));
            fillInBlankVO.setUserAnswer(StrUtil.trim(fillInBlank.getUserAnswers()));
            fillInBlankVOList.add(fillInBlankVO);
        }
        //按创建时间降序排序
        fillInBlankVOList.sort((a, b) -> b.getCreatedTime().compareTo(a.getCreatedTime()));
        IPage<FillInBlankVO> fillInBlankVOIPage = MpListPageUtil.getPage(fillInBlankVOList, page, size);
        return Result.ok(fillInBlankVOIPage);
    }

    /**
     * 开始对应学科错题循环练习
     *
     * @param studentId
     * @param subjectId
     * @param questionCount
     * @return
     */
    private List<ErrorQuestionsVO> initPractice(Long studentId, Integer subjectId, int questionCount,Integer  questionType) {
        try {
            // 获取随机错题
            List<Integer> questions = getRandomErrorQuestions(studentId, subjectId, questionCount,questionType);
            if (questions.isEmpty()) {
                return List.of();
            }
            // 创建练习会话
            PracticeSession session = new PracticeSession();
            session.setStudentId(studentId);
            session.setSubjectId(subjectId);
            session.setQuestionQueue(JSONUtil.toJsonStr(questions));
            session.setWrongQuestions(JSONUtil.toJsonStr(new ArrayList<Integer>()));
            session.setCurrentBatch(1);
            session.setTotalBatches(0);
            session.setCompleted(0);

            practiceSessionMapper.insert(session);
            //获取题目信息
            List<ErrorQuestionsVO> questionsBatch = getQuestionsBatch(questions);
//            Map<Long, List<ErrorQuestionsVO>> map=new HashMap<>();
//            map.put(session.getId(),questionsBatch);
            return questionsBatch;
        } catch (Exception e) {
            log.error("初始化练习会话失败", e);
            return List.of();
        }
    }

    /**
     * 提交答案
     * @param sessionId
     * @param questionId
     * @param isCorrect
     * @return
     */
    @Override
    @Transactional
    public Result<PracticeNextVO> submitAnswer(Long sessionId, Integer questionId, boolean isCorrect) {
        try {
            // 获取会话信息
            PracticeSession session = practiceSessionMapper.selectById(sessionId);
            if (session == null) {
                return Result.error("练习会话不存在");
            }

            if (session.getCompleted() == 1) {
                return Result.error("练习已完成");
            }

            List<Integer> questionQueue = JSONUtil.toList(session.getQuestionQueue(), Integer.class);
            Set<Integer> wrongQuestions = new LinkedHashSet<>(JSONUtil.toList(session.getWrongQuestions(), Integer.class));
            if (questionQueue.isEmpty()) {
                return Result.error("没有待答题目");
            }
            // 处理答题结果
            if (!isCorrect) {
                wrongQuestions.add(questionId);
            }
            // 移除已答题目
            questionQueue.remove(0);
            PracticeNextVO result = new PracticeNextVO();
            result.setRemainingCount(questionQueue.size());
            if (questionQueue.isEmpty()) {
                // 当前批次完成
                if (wrongQuestions.isEmpty()) {
                    // 全部答对
                    session.setCompleted(1);
                    session.setTotalBatches(session.getTotalBatches() + 1);
                    result.setBatchCompleted(true);
                    result.setAllCompleted(true);
                    result.setNextQuestion(null);
                } else {
                    // 有错题，重新开始
                    questionQueue = new ArrayList<>(wrongQuestions);
                    wrongQuestions.clear();
                    session.setCurrentBatch(session.getCurrentBatch() + 1);
                    session.setTotalBatches(session.getTotalBatches() + 1);
                    result.setBatchCompleted(true);
                    result.setAllCompleted(false);
                    result.setNextQuestion(questionQueue.isEmpty() ? null : questionQueue.get(0));
                }
            } else {
                // 还有题目未答完
                result.setBatchCompleted(false);
                result.setAllCompleted(false);
                result.setNextQuestion(questionQueue.get(0));
            }
            // 更新会话状态
            session.setQuestionQueue(JSONUtil.toJsonStr(questionQueue));
            session.setWrongQuestions(JSONUtil.toJsonStr(new ArrayList<>(wrongQuestions)));
            practiceSessionMapper.updateById(session);
            return Result.ok(result);
        } catch (Exception e) {
            log.error("提交答案失败", e);
            return Result.error("提交失败");
        }
    }

    /**
     * 获取练习当前状态（用于恢复进度）
     */
    @Override
    public Result<PracticeStateVO> getPracticeState(Long sessionId) {
        try {
            PracticeSession session = practiceSessionMapper.selectById(sessionId);
            if (session == null) {
                return Result.error("练习会话不存在");
            }
            List<Integer> questionQueue = JSONUtil.toList(session.getQuestionQueue(), Integer.class);
            PracticeStateVO state = new PracticeStateVO();
            state.setSessionId(session.getId());
            state.setSubjectId(session.getSubjectId());
            state.setCurrentBatch(session.getCurrentBatch());
            state.setTotalBatches(session.getTotalBatches());
            state.setCompleted(Objects.equals(session.getCompleted(), 1));
            state.setRemainingCount(questionQueue.size());
            state.setNextQuestion(questionQueue.isEmpty() ? null : questionQueue.get(0));
            List<ErrorQuestionsVO> questionsBatch = getQuestionsBatch(questionQueue);
            state.setPendingQueue(questionsBatch);
            return Result.ok(state);
        } catch (Exception e) {
            log.error("获取练习状态失败", e);
            return Result.error("获取状态失败");
        }
    }

    /**
     * 查找该学生该学科未完成的会话（如有则返回状态，否则新建）
     */
    @Override
    public Result<PracticeStateVO> resumeOrStart(Long studentId, Integer subjectId, int questionCount, Integer questionType) {
        try {
            PracticeSession existing = practiceSessionMapper.selectOne(
                    new QueryWrapper<PracticeSession>()
                            .eq("student_id", studentId)
                            .eq("subject_id", subjectId)
                            .eq("completed", 0)
                            .orderByDesc("update_time")
                            .last("limit 1")
            );
            if (existing != null) {
                return getPracticeState(existing.getId());
            }
            List<ErrorQuestionsVO> init = initPractice(studentId, subjectId, questionCount, questionType);
            if (init == null) {
                return Result.error("初始化失败");
            }
            PracticeSession newest = practiceSessionMapper.selectOne(
                    new QueryWrapper<PracticeSession>()
                            .eq("student_id", studentId)
                            .eq("subject_id", subjectId)
                            .eq("completed", 0)
                            .orderByDesc("id")
                            .last("limit 1")
            );
            if (newest == null) {
                return Result.error("初始化会话失败");
            }
            return getPracticeState(newest.getId());
        } catch (Exception e) {
            log.error("恢复或开始练习失败", e);
            return Result.error("恢复或开始失败");
        }
    }

    /**
     * 批量获取题目详情
     *
     * @param questionIds
     * @return
     */
    private List<ErrorQuestionsVO> getQuestionsBatch(List<Integer> questionIds) {
        // 创建顺序映射
        Map<Integer, Integer> orderMap = new HashMap<>();
        for (int i = 0; i < questionIds.size(); i++) {
            orderMap.put(questionIds.get(i), i);
        }

        List<ErrorQuestions> errorQuestions = errorQuestionsMapper.selectBatchIds(questionIds);

        List<ErrorQuestionsVO> errorQuestionsVOS = new ArrayList<>();
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
                List<String> optionList = parseOptions(choices);
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
                String choices = StrUtil.trim(trueFalse.getOptions());
                List<String> optionList = parseOptions(choices);
                errorQuestionsVO.setOptions(optionList);
                errorQuestionsVO.setCorrectOption(StrUtil.trim(trueFalse.getCorrectResult()));
                errorQuestionsVO.setUserAnswer(StrUtil.trim(trueFalse.getUserAnswer()));
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
        // 按照请求顺序排序
        errorQuestionsVOS.sort((a, b) -> {
            Integer orderA = orderMap.get(a.getQuestionId());
            Integer orderB = orderMap.get(b.getQuestionId());
            return orderA.compareTo(orderB);
        });

        log.info("{}",errorQuestionsVOS);
        return errorQuestionsVOS;
    }

    /**
     * 获取指定数量的随机错题
     *
     * @param studentId
     * @param subjectId
     * @param questionCount
     * @param questionType
     * @return
     */
    private List<Integer> getRandomErrorQuestions(Long studentId, Integer subjectId, int questionCount, Integer questionType) {
        switch (questionType){
            case 1:
                List<ErrorQuestions> errorQuestions = errorQuestionsMapper.selectList(new LambdaQueryWrapper<ErrorQuestions>()
                        .eq(ErrorQuestions::getUserId, studentId)
                        .eq(ErrorQuestions::getSubjectId, subjectId)
                        .eq(ErrorQuestions::getIsMastered, false)
                        .last("ORDER BY RAND() LIMIT " + questionCount));
                return errorQuestions.stream().map(ErrorQuestions::getQuestionId).toList();
            case 2:
                List<SingleChoice> singleQuestions = singleChoiceMapper.selectList(new LambdaQueryWrapper<SingleChoice>()
                        .eq(SingleChoice::getUserId, studentId)
                        .eq(SingleChoice::getSubjectId, subjectId)
                        .last("ORDER BY RAND() LIMIT " + questionCount));
                return singleQuestions.stream().map(SingleChoice::getQuestionId).toList();
            case 3:
                List<MultipleChoice> multipleQuestions = multipleChoiceMapper.selectList(new LambdaQueryWrapper<MultipleChoice>()
                        .eq(MultipleChoice::getUserId, studentId)
                        .eq(MultipleChoice::getSubjectId, subjectId)
                        .last("ORDER BY RAND() LIMIT " + questionCount));
                return multipleQuestions.stream().map(MultipleChoice::getQuestionId).toList();
            case 4:
                List<TrueFalse> trueFalseQuestions = trueFalseMapper.selectList(new LambdaQueryWrapper<TrueFalse>()
                        .eq(TrueFalse::getUserId, studentId)
                        .eq(TrueFalse::getSubjectId, subjectId)
                        .last("ORDER BY RAND() LIMIT " + questionCount));
                return trueFalseQuestions.stream().map(TrueFalse::getQuestionId).toList();
            case 5:
                List<FillInBlank> fillInBlankQuestions = fillInBlankMapper.selectList(new LambdaQueryWrapper<FillInBlank>()
                        .eq(FillInBlank::getUserId, studentId)
                        .eq(FillInBlank::getSubjectId, subjectId)
                        .last("ORDER BY RAND() LIMIT " + questionCount));
                return fillInBlankQuestions.stream().map(FillInBlank::getQuestionId).toList();
            default:
                log.error("Invalid question type: {}", questionType);
                return List.of();
        }

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
        vo.setCorrectResult(StrUtil.blankToDefault(vo.getCorrectResult(), ""));
        vo.setTrueFalseUserAnswer(StrUtil.blankToDefault(vo.getTrueFalseUserAnswer(), ""));
    }

    /**
     * 解析选项字符串，按选项间的逗号分割，而不是题目内容中的逗号
     * @param options 选项字符串，格式如 "A. 选项1,B. 选项2,C. 选项3,D. 选项4"
     * @return 选项列表
     */
    private List<String> parseOptions(String options) {
        List<String> optionList = new ArrayList<>();
        if (StrUtil.isBlank(options)) {
            return optionList;
        }

        // 使用正则表达式按选项标识符分割，如 A., B., C., D. 等
        String[] parts = options.split(",(?=[A-Z]\\.\\s*)");

        for (int i = 0; i < parts.length; i++) {
            String option = parts[i].trim();
            // 如果不是以字母.开头，说明是第一个选项被正确分割了，或者是连续的选项
            if (!option.matches("^[A-Z]\\..*")) {
                // 如果是第一个元素且没有选项前缀，添加选项标识
                if (i == 0) {
                    optionList.add(option);
                } else {
                    // 否则尝试恢复选项标识
                    optionList.add((char)('A' + i) + ". " + option);
                }
            } else {
                optionList.add(option);
            }
        }

        // 如果没有按预期分割，返回原始字符串作为一个选项
        if (optionList.size() <= 1 && options.contains(",")) {
            // 更智能地处理选项分割
            return smartParseOptions(options);
        }

        return optionList;
    }

    /**
     * 更智能的选项解析方法
     * @param options 选项字符串
     * @return 选项列表
     */
    private List<String> smartParseOptions(String options) {
        List<String> result = new ArrayList<>();
        if (StrUtil.isBlank(options)) {
            return result;
        }

        // 查找常见的选项标识符模式
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("([A-Z])\\.\\s*([^,]*,?)");
        java.util.regex.Matcher matcher = pattern.matcher(options + ",");

        while (matcher.find()) {
            String option = matcher.group(1) + ". " + matcher.group(2).replaceAll(",$", "").trim();
            if (!option.endsWith(".")) {  // 避免添加空选项
                result.add(option);
            }
        }

        // 如果没有匹配到预期的模式，返回原始字符串
        if (result.isEmpty()) {
            result.add(options);
        }

        return result;
    }

}




