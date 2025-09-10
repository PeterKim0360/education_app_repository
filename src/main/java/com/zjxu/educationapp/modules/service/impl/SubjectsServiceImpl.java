package com.zjxu.educationapp.modules.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjxu.educationapp.common.constant.QuestionsType;
import com.zjxu.educationapp.common.constant.Subject;
import com.zjxu.educationapp.common.utils.MpListPageUtil;
import com.zjxu.educationapp.common.utils.Result;
import com.zjxu.educationapp.modules.entity.Subjects;
import com.zjxu.educationapp.modules.service.SubjectsService;
import com.zjxu.educationapp.modules.mapper.SubjectsMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
* @author huawei
* @description 针对表【subjects】的数据库操作Service实现
* @createDate 2025-09-08 00:59:25
*/
@Service
@Slf4j
public class SubjectsServiceImpl extends ServiceImpl<SubjectsMapper, Subjects>
    implements SubjectsService{
    @Autowired
    private SubjectsMapper subjectsMapper;
    /**
     * 错题默认页面响应
     * @return
     */
    @Override
    public Result<IPage<Map<String, Map<Integer, String>>>> responseDefault(int page, int size) {
        List<Map<String, Map<Integer, String>>> subjectsList = Subject.toList();
        log.info("{}",subjectsList);
        IPage<Map<String, Map<Integer, String>>> subjectsIPage = MpListPageUtil.getPage(subjectsList, page, size);
        return Result.ok(subjectsIPage);
    }

    /**
     * AI生题提供的科目类型
     * @return
     */
    @Override
    public Result<IPage<Map<Integer,String>>> queryAIQuestions(int page, int size) {
        List<Map<Integer, String>> listSubjectName = Subject.toListSubjectName();
        log.info("{}",listSubjectName);
        IPage<Map<Integer, String>> mapIPage = MpListPageUtil.getPage(listSubjectName, page, size);
        return Result.ok(mapIPage);
    }

    /**
     * AI生题提供的题目类型
     * @return
     */
    @Override
    public Result<List<String>> queryAIType() {
        List<String> list = QuestionsType.toList();
        log.info("{}",list);
        return Result.ok(list);
    }
}




