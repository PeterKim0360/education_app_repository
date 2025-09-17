package com.zjxu.educationapp.modules.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjxu.educationapp.modules.entity.TeacherSubject;
import com.zjxu.educationapp.modules.service.TeacherSubjectService;
import com.zjxu.educationapp.modules.mapper.TeacherSubjectMapper;
import org.springframework.stereotype.Service;

/**
* @author huawei
* @description 针对表【teacher_subject(教师 - 学科关联表)】的数据库操作Service实现
* @createDate 2025-09-17 16:37:22
*/
@Service
public class TeacherSubjectServiceImpl extends ServiceImpl<TeacherSubjectMapper, TeacherSubject>
    implements TeacherSubjectService{

}




