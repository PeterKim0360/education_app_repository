package com.zjxu.educationapp.modules.service.impl;

import com.zjxu.educationapp.common.utils.Result;
import com.zjxu.educationapp.modules.mapper.SubjectsMapper;
import com.zjxu.educationapp.modules.service.StudentCoursesService;
import com.zjxu.educationapp.modules.vo.StudentSubjectsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentCoursesServiceImpl implements StudentCoursesService {
    @Autowired
    private SubjectsMapper subjectsMapper;
    /**
     * 查询所有课程
     * @return
     */
    @Override
    public Result<List<StudentSubjectsVO>> querySubjects() {
        //TODO
        return null;
    }
}
