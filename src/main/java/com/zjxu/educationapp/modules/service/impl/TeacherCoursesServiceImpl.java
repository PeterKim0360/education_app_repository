package com.zjxu.educationapp.modules.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.zjxu.educationapp.common.utils.Result;
import com.zjxu.educationapp.modules.dto.HomeworkInClassDTO;
import com.zjxu.educationapp.modules.dto.HomeworkInClassEditDTO;
import com.zjxu.educationapp.modules.dto.HomeworkInClassSendDTO;
import com.zjxu.educationapp.modules.dto.TeacherSendFileDTO;
import com.zjxu.educationapp.modules.entity.*;
import com.zjxu.educationapp.modules.mapper.*;
import com.zjxu.educationapp.modules.service.TeacherCoursesService;
import com.zjxu.educationapp.modules.vo.HomeworkInClassVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Autowired
    private HomeworkInClassMapper homeworkInClassMapper;
    @Autowired
    private StudentClassMapper studentClassMapper;
    @Autowired
    private HomeworkInClassStuMapper homeworkInClassStuMapper;
    @Autowired
    private GroupTeamMapper groupTeamMapper;

    /**
     * 上传课件
     * @param teacherSendFileDTO
     * @return
     */
    @Override
    public Result<?> uploadFile(TeacherSendFileDTO teacherSendFileDTO) {
        List<Long> classIds = teacherSendFileDTO.getClassIds();
        if (CollectionUtils.isEmpty(classIds)){
            log.error("参数为空");
            return Result.error();
        }
        long teacherId = StpUtil.getLoginIdAsLong();
        //获取对应班级的学生ids
        List<Long> studentIds = studentClassMapper.selectList(new LambdaQueryWrapper<StudentClassEntity>()
                        .in(StudentClassEntity::getClassId, classIds))
                .stream()
                .map(StudentClassEntity::getStudentId).toList();
        for (Long studentId : studentIds) {
            CourseHistory courseHistory = new CourseHistory();
            courseHistory.setStudentId(studentId);
            courseHistory.setTeacherId(teacherId);
            courseHistory.setSubjectId(teacherSendFileDTO.getSubjectId());
            courseHistory.setFileUrl(teacherSendFileDTO.getFileUrl());
            courseHistory.setFileDescription(teacherSendFileDTO.getFileDescription());
            courseHistory.setUploadTime(new Date());
            courseHistoryMapper.insert(courseHistory);
        }
        return Result.ok();
    }

    /**
     * 开始上课
     *
     * @param subjectId
     * @return
     */
    @Override
    public Result<?> startClass(Integer subjectId) {
        long teacherId = StpUtil.getLoginIdAsLong();
        List<SubjectClassTeach> subjectClassTeaches = subjectClassTeachMapper.selectList(new LambdaQueryWrapper<SubjectClassTeach>()
                .eq(SubjectClassTeach::getSubjectId, subjectId)
                .eq(SubjectClassTeach::getTeachId, teacherId)
                .eq(SubjectClassTeach::getStatus, 0));
        for (SubjectClassTeach subjectClassTeach : subjectClassTeaches) {
            subjectClassTeach.setStatus(1);
            subjectClassTeachMapper.update(subjectClassTeach,new LambdaQueryWrapper<SubjectClassTeach>()
                    .eq(SubjectClassTeach::getSubjectId, subjectId)
                    .eq(SubjectClassTeach::getTeachId, teacherId)
                    .eq(SubjectClassTeach::getClassId,subjectClassTeach.getClassId()));
        }
        return Result.ok();
    }

    /**
     * 结束上课
     *
     * @param subjectId
     * @return
     */
    @Override
    public Result<?> endClass(Integer subjectId) {
        long teacherId = StpUtil.getLoginIdAsLong();
        //如果有进行小组分组的话，需要将小组删除
        List<GroupTeam> groupTeams = groupTeamMapper.selectList(new LambdaQueryWrapper<GroupTeam>()
                .eq(GroupTeam::getSubjectId, subjectId)
                .eq(GroupTeam::getCreatedBy, teacherId)
                .eq(GroupTeam::getLogicalDel, 0));
        if (CollectionUtils.isNotEmpty(groupTeams)){
            for (GroupTeam groupTeam : groupTeams) {
                groupTeam.setLogicalDel(1);
                groupTeamMapper.updateById(groupTeam);
            }
        }
        List<SubjectClassTeach> subjectClassTeaches = subjectClassTeachMapper.selectList(new LambdaQueryWrapper<SubjectClassTeach>()
                .eq(SubjectClassTeach::getSubjectId, subjectId)
                .eq(SubjectClassTeach::getTeachId, teacherId)
                .eq(SubjectClassTeach::getStatus, 1));
        for (SubjectClassTeach subjectClassTeach : subjectClassTeaches) {
            subjectClassTeach.setStatus(0);
            subjectClassTeachMapper.update(subjectClassTeach,new LambdaQueryWrapper<SubjectClassTeach>()
                    .eq(SubjectClassTeach::getSubjectId, subjectId)
                    .eq(SubjectClassTeach::getTeachId, teacherId)
                    .eq(SubjectClassTeach::getClassId,subjectClassTeach.getClassId()));
        }
        return Result.ok();
    }

    /**
     * 创建随堂作业
     * @param homeworkInClassDTO
     * @return
     */
    @Override
    @Transactional
    public Result<?> createWork(HomeworkInClassDTO homeworkInClassDTO) {
        if (homeworkInClassDTO==null){
            log.error("参数为空");
            return Result.error();
        }
        HomeworkInClass homeworkInClass = new HomeworkInClass();
        BeanUtils.copyProperties(homeworkInClassDTO,homeworkInClass);
        homeworkInClass.setUpdateTime(new Date());
        int result = homeworkInClassMapper.insert(homeworkInClass);
        if (result==0){
            log.error("创建作业失败");
            return Result.error();
        }
        return Result.ok();
    }

    /**
     * 编辑随堂作业
     * @param homeworkInClassEditDTO
     * @return
     */
    @Override
    public Result<?> editWork(HomeworkInClassEditDTO homeworkInClassEditDTO) {
        if (homeworkInClassEditDTO==null){
            log.error("参数为空");
            return Result.error();
        }
        HomeworkInClass homeworkInClass = new HomeworkInClass();
        BeanUtils.copyProperties(homeworkInClassEditDTO,homeworkInClass);
        homeworkInClass.setUpdateTime(new Date());
        int result = homeworkInClassMapper.updateById(homeworkInClass);
        if (result==0){
            log.error("编辑作业失败");
            return Result.error();
        }
        return Result.ok();
    }

    /**
     * 查看随堂作业
     * @param subjectId
     * @return
     */
    @Override
    public Result<List<HomeworkInClassVO>> work(Integer subjectId) {
        //校验
        if (subjectId==null){
            log.error("参数为空");
            return Result.error();
        }
        long teacherId = StpUtil.getLoginIdAsLong();
        List<HomeworkInClass> homeworkInClassList = homeworkInClassMapper.selectList(new LambdaQueryWrapper<HomeworkInClass>()
                .eq(HomeworkInClass::getSubjectId, subjectId)
                .eq(HomeworkInClass::getTeacherId, teacherId)
                .orderByDesc(HomeworkInClass::getUpdateTime));
        List<HomeworkInClassVO> homeworkInClassVOList = homeworkInClassList.stream().map(homeworkInClass -> {
            HomeworkInClassVO homeworkInClassVO = new HomeworkInClassVO();
            BeanUtils.copyProperties(homeworkInClass,homeworkInClassVO);
            homeworkInClassVO.setWorkUrls(JSONUtil.toList(homeworkInClass.getWorkUrls(), String.class));
            return homeworkInClassVO;
        }).toList();
        return Result.ok(homeworkInClassVOList);
    }

    /**
     * 发布随堂作业
     * @param homeworkInClassSendDTO
     * @return
     */
    @Override
    @Transactional
    public Result<?> sendWork(HomeworkInClassSendDTO homeworkInClassSendDTO) {
        if (homeworkInClassSendDTO==null) {
            log.error("参数为空");
            return Result.error();
        }
        HomeworkInClass homeworkInClass = new HomeworkInClass();
        BeanUtils.copyProperties(homeworkInClassSendDTO,homeworkInClass);
        homeworkInClass.setPublishTime(new Date());
        homeworkInClass.setStatus(1);
        homeworkInClass.setUpdateTime(new Date());
        homeworkInClass.setId(homeworkInClassSendDTO.getId());
        log.info("发布作业：{}",homeworkInClass);
        homeworkInClassMapper.updateById(homeworkInClass);

        Long teacherId = homeworkInClassSendDTO.getTeacherId();
        Integer subjectId = homeworkInClassSendDTO.getSubjectId();
        List<SubjectClassTeach> subjectClassTeaches = subjectClassTeachMapper.selectList(new LambdaQueryWrapper<SubjectClassTeach>()
                .eq(SubjectClassTeach::getSubjectId, subjectId)
                .eq(SubjectClassTeach::getTeachId, teacherId)
                .eq(SubjectClassTeach::getStatus, 1));
        List<Long> classIds = subjectClassTeaches.stream().map(SubjectClassTeach::getClassId).toList();
        log.info("班级ID集合：{}",classIds);
        for (Long classId : classIds) {
            List<StudentClassEntity> studentClassEntities = studentClassMapper.selectList(new LambdaQueryWrapper<StudentClassEntity>()
                    .eq(StudentClassEntity::getClassId, classId)
                    .eq(StudentClassEntity::getStatus, 1));
            List<Long> studentIds = studentClassEntities.stream().map(StudentClassEntity::getStudentId).toList();
            log.info("学生ID集合：{}", studentIds);
            for (Long studentId : studentIds) {
                HomeworkInClassStu homeworkInClassStu = new HomeworkInClassStu();
                homeworkInClassStu.setDeadTime(homeworkInClassSendDTO.getDeadTime());
                homeworkInClassStu.setTitle(homeworkInClassSendDTO.getTitle());
                homeworkInClassStu.setWorkUrls(JSONUtil.toJsonStr(homeworkInClassSendDTO.getWorkUrls()));
                homeworkInClassStu.setSubjectId(homeworkInClassSendDTO.getSubjectId());
                homeworkInClassStu.setStudentId(studentId);
                homeworkInClassStuMapper.insert(homeworkInClassStu);
            }
        }
        return Result.ok();
    }



}
