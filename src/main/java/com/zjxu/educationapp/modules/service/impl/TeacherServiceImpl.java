package com.zjxu.educationapp.modules.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zjxu.educationapp.common.constant.ErrorCode;
import com.zjxu.educationapp.common.utils.Result;
import com.zjxu.educationapp.modules.entity.Class;
import com.zjxu.educationapp.modules.entity.SubjectClassTeach;
import com.zjxu.educationapp.modules.entity.Subjects;
import com.zjxu.educationapp.modules.mapper.ClassMapper;
import com.zjxu.educationapp.modules.mapper.SubjectClassTeachMapper;
import com.zjxu.educationapp.modules.mapper.SubjectsMapper;
import com.zjxu.educationapp.modules.service.TeacherService;
import com.zjxu.educationapp.modules.vo.SubjectsVO;
import com.zjxu.educationapp.modules.vo.TeacherClassVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TeacherServiceImpl implements TeacherService {

    @Autowired
    private SubjectsMapper subjectsMapper;
    @Autowired
    private SubjectClassTeachMapper  subjectClassTeachMapper;
    @Autowired
    private ClassMapper classMapper;


    /**
     * 获取老师对应的学科
     * @return
     */
    @Override
    public Result<List<SubjectsVO>> getSubjects() {
        //获取当前教师对应的ID
        long userId = StpUtil.getLoginIdAsLong();
        //获取当前教师对应的学科ID
        List<SubjectClassTeach> subjectClassTeaches = subjectClassTeachMapper.selectList(new LambdaQueryWrapper<SubjectClassTeach>().eq(SubjectClassTeach::getTeachId, userId));
        List<Integer> subjectIds = subjectClassTeaches.stream().map(SubjectClassTeach::getSubjectId).toList();
        //封装返回数据
        List<SubjectsVO> subjectsVOS=new ArrayList<>();
        for (Integer subjectId : subjectIds) {
            SubjectsVO subjectsVO = new SubjectsVO();
            subjectsVO.setSubjectId(subjectId);
            //通过学科ID获取学科名称
            Subjects subjects = subjectsMapper.selectById(subjectId);
            subjectsVO.setSubjectName(subjects.getSubjectName()==null?"未知学科":subjects.getSubjectName());
            subjectsVOS.add(subjectsVO);
        }
        return Result.ok(subjectsVOS);
    }

    /**
     * 获取老师对应学科的班级
     * @param subjectId
     * @return
     */
    @Override
    public Result<List<TeacherClassVO>> getClazz(Integer subjectId) {
        long userId = StpUtil.getLoginIdAsLong();
        List<SubjectClassTeach> classTeaches = subjectClassTeachMapper.selectList(new LambdaQueryWrapper<SubjectClassTeach>()
                .eq(SubjectClassTeach::getTeachId, userId)
                .eq(SubjectClassTeach::getSubjectId, subjectId));
        List<TeacherClassVO> teacherClassVOS=new ArrayList<>();
        for (SubjectClassTeach classTeach : classTeaches) {
            TeacherClassVO teacherClassVO = new TeacherClassVO();
            teacherClassVO.setClassId(classTeach.getClassId());
            //根据班级ID获取班级名称
            Class aClass = classMapper.selectById(classTeach.getClassId());
            teacherClassVO.setClassName(aClass.getClassName()==null?"未知班级":aClass.getClassName());
            teacherClassVOS.add(teacherClassVO);
        }
       return Result.ok(teacherClassVOS);
    }


    /**
     * 创建班级
     *
     * @param className
     * @param subjectId
     * @return
     */
    @Override
    public Result<?> createClass(String className, Integer subjectId) {
        //获取当前教师ID
        long userId = StpUtil.getLoginIdAsLong();
        //创建班级
        Class aClass = new Class();
        aClass.setClassName(className);
        classMapper.insert(aClass);
        //根据班级名获取班级ID
        Class clazz = classMapper.selectOne(new LambdaQueryWrapper<Class>().eq(Class::getClassName, className));
        Long classId = clazz.getClassId();
        SubjectClassTeach subjectClassTeach = new SubjectClassTeach();
        subjectClassTeach.setSubjectId(subjectId);
        subjectClassTeach.setClassId(classId);
        subjectClassTeach.setTeachId(userId);
        subjectClassTeachMapper.insert(subjectClassTeach);
        return Result.ok();
    }

    /**
     * 查看对应班级的学生
     *
     * @param classId
     * @return
     */
    @Override
    public Result<IPage> stuList(Long classId) {
        //TODO
        return null;
    }
}
