package com.zjxu.educationapp.modules.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjxu.educationapp.common.utils.Result;
import com.zjxu.educationapp.modules.entity.Class;
import com.zjxu.educationapp.modules.entity.SubjectClass;
import com.zjxu.educationapp.modules.entity.TeacherSubject;
import com.zjxu.educationapp.modules.mapper.SubjectClassMapper;
import com.zjxu.educationapp.modules.mapper.TeacherSubjectMapper;
import com.zjxu.educationapp.modules.service.ClassService;
import com.zjxu.educationapp.modules.mapper.ClassMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author huawei
* @description 针对表【class(班级表)】的数据库操作Service实现
* @createDate 2025-09-17 16:22:03
*/
@Service
@Slf4j
public class ClassServiceImpl extends ServiceImpl<ClassMapper, Class>
    implements ClassService{

    @Autowired
    private ClassMapper classMapper;
    @Autowired
    private SubjectClassMapper subjectClassMapper;
   @Autowired
   private TeacherSubjectMapper teacherSubjectMapper;
    /**
     * 创建班级
     *
     * @param className
     * @param subjectId
     * @return
     */
    @Override
    public Result<?> createClass(String className, Integer subjectId) {
        Class aClass = new Class();
        aClass.setClassName(className);
        //保存到class表
        classMapper.insert(aClass);
        //保存到class_subject表
        SubjectClass subjectClass = new SubjectClass();
        subjectClass.setClassId(aClass.getClassId());
        subjectClass.setSubjectId(subjectId);
        subjectClassMapper.insert(subjectClass);
        //通过教室ID查看他教的学科
        List<TeacherSubject> teacherSubjects = teacherSubjectMapper.selectList(new LambdaQueryWrapper<TeacherSubject>()
                .eq(TeacherSubject::getTeacherId, StpUtil.getLoginIdAsLong()));
        for (TeacherSubject teacherSubject : teacherSubjects) {
            if (teacherSubject.getSubjectId().equals(subjectId)) {
                log.info("该老师已教过该学科");
                return Result.ok();
            }
        }
        //保存到teach_subject表
        TeacherSubject teacherSubject = new TeacherSubject();
        teacherSubject.setTeacherId(StpUtil.getLoginIdAsLong());
        teacherSubject.setSubjectId(subjectId);
        teacherSubjectMapper.insert(teacherSubject);
        return Result.ok();
    }



}




