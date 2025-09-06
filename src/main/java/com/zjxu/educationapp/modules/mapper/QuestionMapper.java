package com.zjxu.educationapp.modules.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zjxu.educationapp.modules.entity.Question;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface QuestionMapper extends BaseMapper<Question> {
    // 分页查询指定课程类型的题目
    Page<Question> selectQuestionByCourseType(
            Page<Question> page,
            @Param("courseType") Integer courseType);
}