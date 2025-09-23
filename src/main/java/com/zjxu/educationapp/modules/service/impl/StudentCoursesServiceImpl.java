package com.zjxu.educationapp.modules.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zjxu.educationapp.common.utils.Result;
import com.zjxu.educationapp.modules.entity.*;
import com.zjxu.educationapp.modules.mapper.*;
import com.zjxu.educationapp.modules.service.StudentCoursesService;
import com.zjxu.educationapp.modules.vo.StuSubjectDetailVO;
import com.zjxu.educationapp.modules.vo.StudentSubjectsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class StudentCoursesServiceImpl implements StudentCoursesService {
    @Autowired
    private SubjectsMapper subjectsMapper;
    @Autowired
    private StudentClassMapper studentClassMapper;
    @Autowired
    private SubjectClassTeachMapper subjectClassTeachMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private CourseHistoryMapper courseHistoryMapper;
    /**
     * 查询所有课程
     * @return
     */
    @Override
    public Result<List<StudentSubjectsVO>> querySubjects() {
        //获取当前用户ID
        long userId = StpUtil.getLoginIdAsLong();
        //根据学生ID查询班级ID
        StudentClassEntity studentClass = studentClassMapper.selectOne(new LambdaQueryWrapper<StudentClassEntity>().
                eq(StudentClassEntity::getStudentId, userId)
                .eq(StudentClassEntity::getStatus, 1));
        Long classId = studentClass.getClassId();
        //根据班级ID查询所有课程ID
        List<SubjectClassTeach> subjectClassTeachList = subjectClassTeachMapper.selectList(new LambdaQueryWrapper<SubjectClassTeach>()
                .eq(SubjectClassTeach::getClassId, classId));
        List<Integer> subjectIds = subjectClassTeachList.stream().map(SubjectClassTeach::getSubjectId).toList();
        List<StudentSubjectsVO> studentSubjectsVOList = new ArrayList<>();
        for (Integer subjectId : subjectIds) {
            StudentSubjectsVO studentSubjectsVO = new StudentSubjectsVO();
            //根据课程ID查询课程名称
            Subjects subject = subjectsMapper.selectOne(new LambdaQueryWrapper<Subjects>()
                    .eq(Subjects::getSubjectId, subjectId));
            studentSubjectsVO.setSubjectName(subject.getSubjectName()==null?"未知学科":subject.getSubjectName());
            studentSubjectsVO.setSubjectId(subjectId);
            //根据课程ID和班级ID查对应教师ID
            SubjectClassTeach subjectClassTeach = subjectClassTeachMapper.selectOne(new LambdaQueryWrapper<SubjectClassTeach>()
                    .eq(SubjectClassTeach::getSubjectId, subjectId)
                    .eq(SubjectClassTeach::getClassId, classId));
            studentSubjectsVO.setStatus(subjectClassTeach.getStatus());
            Long teachId = subjectClassTeach.getTeachId();
            //根据教师ID查教师名和头像
            UserEntity teacher = userMapper.selectById(teachId);
            studentSubjectsVO.setTeacherName(teacher.getUserName()==null?"未知教师":teacher.getUserName());
            studentSubjectsVO.setAvatarUrl(teacher.getAvatarUrl());
            studentSubjectsVOList.add(studentSubjectsVO);
        }
        return Result.ok(studentSubjectsVOList);
    }

    /**
     * 查询课程详情
     *
     * @param subjectId
     * @return
     */
    @Override
    public Result<StuSubjectDetailVO> queryDetail(Integer subjectId) {
        StuSubjectDetailVO detailVO = new StuSubjectDetailVO();
        //根据科目ID查询科目名称
        Subjects subject = subjectsMapper.selectById(subjectId);
        detailVO.setSubjectId(subjectId);
        detailVO.setSubjectName(subject.getSubjectName()==null?"未知学科":subject.getSubjectName());
        //获取当前学生ID
        long studentId = StpUtil.getLoginIdAsLong();
        //根据学生ID查询班级ID
        StudentClassEntity studentClass = studentClassMapper.selectOne(new LambdaQueryWrapper<StudentClassEntity>()
                .eq(StudentClassEntity::getStudentId, studentId)
                .eq(StudentClassEntity::getStatus, 1));
        Long classId = studentClass.getClassId();
        //根据课程ID和班级ID查教师ID
        SubjectClassTeach subjectClassTeach = subjectClassTeachMapper.selectOne(new LambdaQueryWrapper<SubjectClassTeach>()
                .eq(SubjectClassTeach::getClassId, classId)
                .eq(SubjectClassTeach::getSubjectId, subjectId));
        Long teacherId = subjectClassTeach.getTeachId();
        detailVO.setTeacherId(teacherId);
        //根据教师ID查教师名
        UserEntity teacher = userMapper.selectById(teacherId);
        String teacherName = teacher.getUserName();
        detailVO.setTeacherName(teacherName);
        //根据教师ID和课程ID查文件url、文件描述、上传时间
        CourseHistory courseHistory = courseHistoryMapper.selectOne(new LambdaQueryWrapper<CourseHistory>()
                .eq(CourseHistory::getTeacherId, teacherId)
                .eq(CourseHistory::getSubjectId, subjectId));
        detailVO.setFileUrl(courseHistory.getFileUrl()==null?"暂无课程文件":courseHistory.getFileUrl());
        detailVO.setFileDescription(courseHistory.getFileDescription()==null?"":courseHistory.getFileDescription());
        detailVO.setUploadTime(courseHistory.getUploadTime());
        return Result.ok(detailVO);
    }
}
