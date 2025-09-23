package com.zjxu.educationapp.modules.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zjxu.educationapp.common.utils.Result;
import com.zjxu.educationapp.modules.dto.TeacherSendFileDTO;
import com.zjxu.educationapp.modules.entity.ClassEntity;
import com.zjxu.educationapp.modules.entity.CourseHistory;
import com.zjxu.educationapp.modules.entity.SubjectClassTeach;
import com.zjxu.educationapp.modules.mapper.ClassMapper;
import com.zjxu.educationapp.modules.mapper.CourseHistoryMapper;
import com.zjxu.educationapp.modules.mapper.SubjectClassTeachMapper;
import com.zjxu.educationapp.modules.service.TeacherCoursesService;
import com.zjxu.educationapp.modules.vo.TeacherClassVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TeacherCoursesServiceImpl implements TeacherCoursesService {
    @Autowired
    private CourseHistoryMapper courseHistoryMapper;
    @Autowired
    private SubjectClassTeachMapper subjectClassTeachMapper;
    @Autowired
    private ClassMapper classMapper;

    /**
     * 上传课件
     * @param teacherSendFileDTO
     * @return
     */
    @Override
    public Result<?> uploadFile(TeacherSendFileDTO teacherSendFileDTO) {
        long teacherId = StpUtil.getLoginIdAsLong();
        CourseHistory courseHistory = new CourseHistory();
        courseHistory.setTeacherId(teacherId);
        courseHistory.setSubjectId(teacherSendFileDTO.getSubjectId());
        courseHistory.setFileUrl(teacherSendFileDTO.getFileUrl());
        courseHistory.setFileDescription(teacherSendFileDTO.getFileDescription());
        courseHistory.setUploadTime(new Date());
        courseHistoryMapper.insert(courseHistory);
        return Result.ok();
    }

    /**
     * 获取课程对应的班级
     * @param subjectId
     * @return
     */
    @Override
    public Result<List<TeacherClassVO>> getClazz(Integer subjectId) {
        long teacherId = StpUtil.getLoginIdAsLong();
        List<SubjectClassTeach> subjectClassTeaches = subjectClassTeachMapper.selectList(new LambdaQueryWrapper<SubjectClassTeach>()
                .eq(SubjectClassTeach::getTeachId, teacherId)
                .eq(SubjectClassTeach::getSubjectId, subjectId));
        List<Long> classIds = subjectClassTeaches.stream().map(subjectClassTeach -> subjectClassTeach.getClassId()).collect(Collectors.toList());
        List<TeacherClassVO> teacherClassVOS = new ArrayList<>();
        for (Long classId : classIds) {
            TeacherClassVO teacherClassVO = new TeacherClassVO();
            teacherClassVO.setClassId(classId);
            ClassEntity classEntity = classMapper.selectById(classId);
            teacherClassVO.setClassName(classEntity.getClassName()==null?"未知班级":classEntity.getClassName());
            teacherClassVOS.add(teacherClassVO);
        }
        return Result.ok(teacherClassVOS);
    }
}
