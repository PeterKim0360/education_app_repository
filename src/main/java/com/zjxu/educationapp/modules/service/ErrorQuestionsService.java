package com.zjxu.educationapp.modules.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zjxu.educationapp.common.utils.Result;
import com.zjxu.educationapp.modules.dto.ErrorQuestionDTO;
import com.zjxu.educationapp.modules.entity.ErrorQuestions;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zjxu.educationapp.modules.vo.ErrorQuestionsVO;

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

}
