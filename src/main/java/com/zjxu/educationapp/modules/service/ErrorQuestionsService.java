package com.zjxu.educationapp.modules.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zjxu.educationapp.common.utils.Result;
import com.zjxu.educationapp.modules.dto.ErrorQuestionDTO;
import com.zjxu.educationapp.modules.entity.ErrorQuestions;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zjxu.educationapp.modules.vo.*;
import org.springframework.transaction.annotation.Transactional;

/**
* @author huawei
* @description 针对表【error_questions】的数据库操作Service
* @createDate 2025-09-07 17:19:04
*/
public interface ErrorQuestionsService extends IService<ErrorQuestions> {
    /**
     * 错题分页查询
     * @param subjectId
     * @param page
     * @param size
     * @return
     */
    Result<IPage<ErrorQuestionsVO>> queryErrorQuestions(int subjectId, int page, int size);

    /**
     * 错题记载
     * @param errorQuestionDTO
     * @return
     */
    Result<?> insertQuestions(ErrorQuestionDTO errorQuestionDTO);

    /**
     * 取消错题
     * @param questionId
     * @return
     */
    Result<?> ErrorQuestionDel(int questionId);

    /**
     * 单选题查询
     * @param subjectId
     * @param page
     * @param size
     * @return
     */
    Result<IPage<SingleChoiceVO>> querySingleChoice(int subjectId, int page, int size);

    /**
     * 多选题查询
     * @return
     */
    Result<IPage<MultipleChoiceVO>> queryMultipleChoice(Integer subjectId, int page, int size);

    /**
     * 判断题查询
     * @return
     */
    Result<IPage<TrueFalseVO>> queryTrueFalse(Integer subjectId, int page, int size);

    /**
     * 填空题查询
     * @return
     */
    Result<IPage<FillInBlankVO>> queryFillInBlank(Integer subjectId, int page, int size);

//    /**
//     * 开始对应学科错题循环练习
//     *
//     * @param studentId
//     * @param subjectId
//     * @param questionCount
//     * @return
//     */
//    Result<List<ErrorQuestionsVO>> initPractice(Long studentId, Integer subjectId, int questionCount);

    @Transactional
    Result<PracticeNextVO> submitAnswer(Long sessionId, Integer questionId, boolean isCorrect);

    /**
     * 获取练习当前状态（用于恢复进度）
     * @param sessionId
     * @return
     */
    Result<PracticeStateVO> getPracticeState(Long sessionId);

    /**
     * 查找该学生该学科未完成的会话（如有则返回状态，否则新建）
     *
     * @param studentId
     * @param subjectId
     * @param questionCount 当不存在会话时用于初始化的数量
     * @param questionType
     * @return
     */
    Result<PracticeStateVO> resumeOrStart(Long studentId, Integer subjectId, int questionCount, Integer questionType);
//
//    /**
//     * 批量获取题目详情
//     *
//     * @param questionIds
//     * @param subjectId
//     * @return
//     */
//    Result<List<ErrorQuestionsVO>> getQuestionsBatch(List<Integer> questionIds, Integer subjectId);
}
