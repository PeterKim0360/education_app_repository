package com.zjxu.educationapp.modules.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zjxu.educationapp.common.constant.ErrorCode;
import com.zjxu.educationapp.common.utils.Result;
import com.zjxu.educationapp.modules.entity.*;
import com.zjxu.educationapp.modules.entity.ClassEntity;
import com.zjxu.educationapp.modules.mapper.*;
import com.zjxu.educationapp.modules.service.TeacherService;
import com.zjxu.educationapp.modules.vo.StudentSimpleVO;
import com.zjxu.educationapp.modules.vo.SubjectsVO;
import com.zjxu.educationapp.modules.vo.TeacherClassVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TeacherServiceImpl implements TeacherService {

    @Autowired
    private SubjectsMapper subjectsMapper;
    @Autowired
    private SubjectClassTeachMapper  subjectClassTeachMapper;
    @Autowired
    private ClassMapper classMapper;
    @Autowired
    private StudentClassMapper studentClassMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private SubjectClassMapper subjectClassMapper;


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
            ClassEntity aClass = classMapper.selectById(classTeach.getClassId());
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
        ClassEntity aClass = new ClassEntity();
        aClass.setClassName(className);
        classMapper.insert(aClass);
        //根据班级名获取班级ID
        ClassEntity clazz = classMapper.selectOne(new LambdaQueryWrapper<ClassEntity>().eq(ClassEntity::getClassName, className));
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
     * @param page
     * @param size
     * @return
     */
    @Override
    public Result<IPage<StudentSimpleVO>> stuList(Long classId, int page, int size) {
        Page<StudentClassEntity> studentClassPage = studentClassMapper.selectPage(new Page<>(page, size),
                new LambdaQueryWrapper<StudentClassEntity>()
                        .eq(StudentClassEntity::getClassId, classId)
                        .eq(StudentClassEntity::getStatus, 1));
        IPage<StudentSimpleVO> studentSimpleVOIPage = studentClassPage.convert(studentClass -> {
            StudentSimpleVO studentSimpleVO = new StudentSimpleVO();
            //根据学生Id找学生信息
            UserEntity student = userMapper.selectOne(new LambdaQueryWrapper<UserEntity>()
                    .eq(UserEntity::getId, studentClass.getStudentId()));
            studentSimpleVO.setUserName(student.getUserName());
            studentSimpleVO.setUserId(student.getId());
            studentSimpleVO.setAvatarUrl(student.getAvatarUrl());
            return studentSimpleVO;
        });
        return Result.ok(studentSimpleVOIPage);
    }

    /**
     * 删除学生
     *
     * @param stuIds
     * @return
     */
    @Override
    public Result<?> deleteStus(List<Long> stuIds) {
        if (stuIds==null||stuIds.size()==0){
            log.info("未选择要删除的学生");
            return Result.error(ErrorCode.UNSELECTED_FOR_DELETION);
        }
        for (Long stuId : stuIds) {
            StudentClassEntity studentClass = studentClassMapper.selectOne(new LambdaQueryWrapper<StudentClassEntity>()
                    .eq(StudentClassEntity::getStudentId, stuId)
                    .eq(StudentClassEntity::getStatus, 1));
            log.info("删除学生信息：{}",studentClass);
            if (studentClass==null){
                log.info("未找到要删除的学生");
                return Result.error(ErrorCode.STU_NOT_FOUND);
            }
            studentClass.setStatus(0);
            studentClassMapper.update(studentClass,new LambdaQueryWrapper<StudentClassEntity>()
                    .eq(StudentClassEntity::getStudentId, studentClass.getStudentId()));
        }
        return Result.ok();
    }

    /**
     * 添加学生
     **/
    @Override
    public Result<?> addStus(List<Long> stuIds, Long classId) {
        for (Long stuId : stuIds) {
            StudentClassEntity studentClass = new StudentClassEntity();
            studentClass.setStudentId(stuId);
            studentClass.setClassId(classId);
            studentClassMapper.insert(studentClass);
        }
        return Result.ok();
    }

    /**
     * 匹配班级
     * @param subjectIds
     * @return
     */
    @Override
    public Result<?> matchClass(List<Integer> subjectIds) {
        //获取当前教师ID
        long teacherId = StpUtil.getLoginIdAsLong();
        if (subjectIds==null|| subjectIds.isEmpty()){
            log.info("未选择要匹配的学科");
            return Result.error();
        }
        for (Integer subjectId : subjectIds) {
            //根据科目id分别查到3个未被占用的班级id
            List<SubjectClass> subjectClasses = subjectClassMapper.selectList(new LambdaQueryWrapper<SubjectClass>()
                    .eq(SubjectClass::getSubjectId, subjectId)
                    .eq(SubjectClass::getStatus, 0)
                    .last("limit 3"));
            for (SubjectClass subjectClass : subjectClasses) {
                subjectClass.setStatus(1);
                subjectClassMapper.updateById(subjectClass);
            }
            //获取班级Ids
            List<Long> classIds = subjectClasses.stream().map(SubjectClass::getClassId).toList();
            //存入数据库
            for (Long classId : classIds) {
                SubjectClassTeach subjectClassTeach = new SubjectClassTeach();
                subjectClassTeach.setSubjectId(subjectId);
                subjectClassTeach.setClassId(classId);
                subjectClassTeach.setTeachId(teacherId);
                subjectClassTeachMapper.insert(subjectClassTeach);
            }
        }
        return Result.ok();
    }


}
