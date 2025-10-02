package com.zjxu.educationapp.modules.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.zjxu.educationapp.common.utils.Result;
import com.zjxu.educationapp.modules.entity.*;
import com.zjxu.educationapp.modules.mapper.*;
import com.zjxu.educationapp.modules.service.StudentCoursesService;
import com.zjxu.educationapp.modules.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Slf4j
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
    @Autowired
    private HomeworkInClassStuMapper  homeworkInClassStuMapper;
    @Autowired
    private SemesterConfigMapper semesterConfigMapper;
    @Autowired
    private ScheduleMapper scheduleMapper;


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
        log.info("{}", studentClass);
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
            log.info("{}", subject);
            studentSubjectsVO.setSubjectName(subject.getSubjectName()==null?"未知学科":subject.getSubjectName());
            studentSubjectsVO.setSubjectId(subjectId);
            //根据课程ID和班级ID查对应教师ID
            SubjectClassTeach subjectClassTeach = subjectClassTeachMapper.selectOne(new LambdaQueryWrapper<SubjectClassTeach>()
                    .eq(SubjectClassTeach::getSubjectId, subjectId)
                    .eq(SubjectClassTeach::getClassId, classId));
            log.info("{}", subjectClassTeach);
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
        List<String> urls = JSONUtil.toList(courseHistory.getFileUrl(), String.class);
        log.info("文件url:{}", urls);
        List<String> filenames = JSONUtil.toList(courseHistory.getFileDescription(), String.class);
        log.info("文件描述:{}", filenames);
        Map<String ,String> file=new HashMap<>();
        for (int i = 0; i < urls.size(); i++) {
            file.put(filenames.get(i), urls.get(i));
        }
        log.info("文件:{}", file);
        detailVO.setFile(file);
        return Result.ok(detailVO);
    }

    /**
     * 查询随堂作业（只展示刚发布未截止的）
     * @param subjectId
     * @return
     */
    @Override
    public Result<List<HomeworkInClassStuVO>> queryWork(Integer subjectId) {
        long studentId = StpUtil.getLoginIdAsLong();
        log.info("学生ID为{}的作业", studentId);
        List<HomeworkInClassStu> homeworkInClassStus = homeworkInClassStuMapper.selectList(new LambdaQueryWrapper<HomeworkInClassStu>()
                .eq(HomeworkInClassStu::getStudentId, studentId)
                .eq(HomeworkInClassStu::getSubjectId, subjectId)
                .gt(HomeworkInClassStu::getDeadTime,new Date()));
        log.info("学生作业为{}", homeworkInClassStus);
        List<HomeworkInClassStuVO> homeworkInClassStuVOS = homeworkInClassStus.stream().map(homeworkInClassStu -> {
            HomeworkInClassStuVO homeworkInClassStuVO = new HomeworkInClassStuVO();
            BeanUtils.copyProperties(homeworkInClassStu, homeworkInClassStuVO);
            homeworkInClassStuVO.setWorkUrls(JSONUtil.toList(homeworkInClassStu.getWorkUrls(), String.class));
            return homeworkInClassStuVO;
        }).toList();
        log.info("学生封装作业为{}", homeworkInClassStuVOS);
        return Result.ok(homeworkInClassStuVOS);
    }

    /**
     * 查询已截止的作业
     * @param subjectId
     * @return
     */
    @Override
    public Result<List<HomeworkInClassStuVO>> queryExpireWork(Integer subjectId) {
        long studentId = StpUtil.getLoginIdAsLong();
        log.info("学生ID为{}的作业", studentId);
        List<HomeworkInClassStu> homeworkInClassStus = homeworkInClassStuMapper.selectList(new LambdaQueryWrapper<HomeworkInClassStu>()
                .eq(HomeworkInClassStu::getStudentId, studentId)
                .eq(HomeworkInClassStu::getSubjectId, subjectId)
                .le(HomeworkInClassStu::getDeadTime,new Date()));
        log.info("学生作业为{}", homeworkInClassStus);
        List<HomeworkInClassStuVO> homeworkInClassStuVOS = homeworkInClassStus.stream().map(homeworkInClassStu -> {
            HomeworkInClassStuVO homeworkInClassStuVO = new HomeworkInClassStuVO();
            BeanUtils.copyProperties(homeworkInClassStu, homeworkInClassStuVO);
            homeworkInClassStuVO.setWorkUrls(JSONUtil.toList(homeworkInClassStu.getWorkUrls(), String.class));
            return homeworkInClassStuVO;
        }).toList();
        log.info("学生封装作业为{}", homeworkInClassStuVOS);
        return Result.ok(homeworkInClassStuVOS);
    }

    /**
     * 查询课程文件
     *
     * @param subjectId
     * @return
     */
    @Override
    public Result<List<CourseHistoryVO>> queryFileList(Integer subjectId) {
        long studentId = StpUtil.getLoginIdAsLong();
        List<CourseHistory> courseHistories = courseHistoryMapper.selectList(new LambdaQueryWrapper<CourseHistory>()
                .eq(CourseHistory::getSubjectId, subjectId)
                .eq(CourseHistory::getStudentId, studentId)
                .orderByDesc(CourseHistory::getUploadTime));
        List<CourseHistoryVO> courseHistoryVOS = courseHistories.stream().map(courseHistory -> {
            CourseHistoryVO courseHistoryVO = new CourseHistoryVO();
            BeanUtils.copyProperties(courseHistory, courseHistoryVO);
            //获取老师名
            Long teacherId = courseHistory.getTeacherId();
            UserEntity teacher = userMapper.selectById(teacherId);
            courseHistoryVO.setTeacherName(teacher.getUserName());
            //获取课程名
            Subjects subject = subjectsMapper.selectById(subjectId);
            courseHistoryVO.setSubjectName(subject.getSubjectName());
            return courseHistoryVO;
        }).toList();
        return Result.ok(courseHistoryVOS);
    }

//    /**
//     * 切换学期
//     */
//    @Override
//    public Result<Long> changeSemester(Long semesterId) {
//        SemesterConfig semesterConfig = semesterConfigMapper.selectById(semesterId);
//        if (semesterConfig == null){
//            return Result.error("该学期不存在");
//        }
//        //更新当前学期状态
//        semesterConfigMapper.update(null,
//                new LambdaUpdateWrapper<SemesterConfig>()
//                        .set(SemesterConfig::getIsCurrent, 0)
//                        .eq(SemesterConfig::getIsCurrent,1));
//        //设置新学期为当前学期
//        semesterConfig.setIsCurrent(1);
//        semesterConfigMapper.updateById(semesterConfig);
//        return Result.ok(semesterConfig.getId());
//    }

    /**
     * 获取课程安排
     * @return
     */
    @Override
    public Result<List<ScheduleDetailVO>> queryCombinedSimple(String week, String weekday) {
        long studentId = StpUtil.getLoginIdAsLong();
//        SemesterConfig semesterConfig = semesterConfigMapper.selectById(semesterId);
//        if (semesterConfig == null){
//            return Result.error("该学期不存在");
//        }
        //查询指定学期的课表
        List<Schedule> schedules = scheduleMapper.selectList(new LambdaQueryWrapper<Schedule>()
                .eq(Schedule::getWeek, week)
                .eq(StrUtil.isNotEmpty(weekday),Schedule::getWeekday, weekday)
                .eq(Schedule::getUserId, studentId)
                .orderByAsc(Schedule::getWeekday)
                .orderByAsc(Schedule::getCourseTime));
        List<ScheduleDetailVO> scheduleDetailVOList = schedules.stream().map(schedule -> {
            ScheduleDetailVO scheduleDetailVO = new ScheduleDetailVO();
            BeanUtils.copyProperties(schedule, scheduleDetailVO);
            Long teacherId = schedule.getTeacherId();
            UserEntity teacher = userMapper.selectById(teacherId);
            scheduleDetailVO.setTeachName(teacher.getUserName());
            Boolean result = isCurrent(schedule);
            scheduleDetailVO.setIsCurrent(result ? 1 : 0);
            return scheduleDetailVO;
        }).toList();
        return Result.ok(scheduleDetailVOList);
    }

//    /**
//     * 获取课程安排（详细）
//     *
//     * @param courseId
//     * @param semesterId
//     * @return
//     */
//    @Override
//    public Result<ScheduleDetailVO> queryScheduleDetail(Integer courseId, Long semesterId) {
//        long studentId = StpUtil.getLoginIdAsLong();
//        Schedule schedule = scheduleMapper.selectOne(new LambdaQueryWrapper<Schedule>()
//                .eq(Schedule::getCourseId, courseId)
//                .eq(Schedule::getSemesterId, semesterId)
//                .eq(Schedule::getUserId, studentId));
//        if (schedule == null){
//            return Result.error("该课程不存在");
//        }
//        ScheduleDetailVO scheduleDetailVO = new ScheduleDetailVO();
//        BeanUtils.copyProperties(schedule, scheduleDetailVO);
//        UserEntity userEntity = userMapper.selectById(schedule.getTeacherId());
//        scheduleDetailVO.setTeachName(userEntity.getUserName());
//        Boolean result = isCurrent(schedule);
//        scheduleDetailVO.setIsCurrent(result ? 1 : 0);
//        return Result.ok(scheduleDetailVO);
//    }

    /**
     * 是否为当前课程
     */
    private Boolean isCurrent(Schedule schedule) {
        if (schedule == null){
            return false;
        }
        //获取当前时间
        LocalTime now = LocalTime.now();
        String currentTime = DateTimeFormatter.ofPattern("HH:mm").format(now);
        String[] timeRange = schedule.getCourseTime().split("-");
        LocalTime startTime = LocalTime.parse(timeRange[0]);
        LocalTime endTime = LocalTime.parse(timeRange[1]);
        return now.isAfter(startTime.minusMinutes(1)) && now.isBefore(endTime.plusMinutes(1));
    }


//    /**
//     * 学生选课
//     *
//     * @param subjectId
//     * @param classId
//     * @return
//     */
//    @Override
//    @Transactional
//    public Result<?> select(Integer subjectId, Long classId) {
//        //判断该课程是否满人了
//        SubjectClass subjectClass = subjectClassMapper.selectOne(new LambdaQueryWrapper<SubjectClass>()
//                .eq(SubjectClass::getSubjectId, subjectId)
//                .eq(SubjectClass::getClassId, classId));
//        Integer studentCount = subjectClass.getStudentCount();
//        if (studentCount > 40){
//            log.info("课程已满");
//            return Result.error("课程已满");
//        }
//        //获取当前学生ID
//        long studentId = StpUtil.getLoginIdAsLong();
//        StudentSubject studentSubject;
//        //查看是否重复选课
//        studentSubject = studentSubjectMapper.selectOne(new LambdaQueryWrapper<StudentSubject>()
//                .eq(StudentSubject::getStudentId, studentId)
//                .eq(StudentSubject::getSubjectId, subjectId)
//                .eq(StudentSubject::getStatus, 1));
//        if (studentSubject != null) {
//            return Result.error("请勿重复选课");
//        }
//        studentSubject = new StudentSubject();
//        studentSubject.setStudentId(studentId);
//        studentSubject.setSubjectId(subjectId);
//        studentSubject.setStatus(1);
//        studentSubjectMapper.insert(studentSubject);
//        //把该学生加入到该课程对应的班级中
//        subjectClass.setStudentCount(studentCount + 1);
//        subjectClassMapper.updateById(subjectClass);
//        StudentClassEntity studentClass = new StudentClassEntity();
//        studentClass.setStudentId(studentId);
//        studentClass.setClassId(classId);
//        studentClassMapper.insert(studentClass);
//        return Result.ok();
//    }
//
//    /**
//     * 取消选课
//     *
//     * @param subjectId
//     * @param classId
//     * @return
//     */
//    @Override
//    @Transactional
//    public Result<?> cancel(Integer subjectId, Long classId) {
//        long studentId = StpUtil.getLoginIdAsLong();
//        StudentSubject studentSubject = studentSubjectMapper.selectOne(new LambdaQueryWrapper<StudentSubject>()
//                .eq(StudentSubject::getStudentId, studentId)
//                .eq(StudentSubject::getSubjectId, subjectId)
//                .eq(StudentSubject::getStatus, 1));
//        studentSubject.setStatus(0);
//        studentSubjectMapper.updateById(studentSubject);
//        StudentClassEntity studentClass = studentClassMapper.selectOne(new LambdaQueryWrapper<StudentClassEntity>()
//                .eq(StudentClassEntity::getStudentId, studentId)
//                .eq(StudentClassEntity::getClassId, classId)
//                .eq(StudentClassEntity::getStatus, 1));
//        studentClass.setStatus(0);
//        studentClassMapper.updateById(studentClass);
//        SubjectClass subjectClass = subjectClassMapper.selectOne(new LambdaQueryWrapper<SubjectClass>()
//                .eq(SubjectClass::getSubjectId, subjectId)
//                .eq(SubjectClass::getClassId, classId));
//        Integer studentCount = subjectClass.getStudentCount();
//        subjectClass.setStudentCount(studentCount - 1);
//        subjectClassMapper.updateById(subjectClass);
//        return Result.ok();
//    }
}
