package com.zjxu.educationapp.modules.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjxu.educationapp.common.constant.ErrorCode;
import com.zjxu.educationapp.common.utils.Result;
import com.zjxu.educationapp.modules.dto.StuHWSubmitDTO;
import com.zjxu.educationapp.modules.entity.StuHomework;
import com.zjxu.educationapp.modules.entity.Subjects;
import com.zjxu.educationapp.modules.entity.TeachHomework;
import com.zjxu.educationapp.modules.mapper.StuHomeworkMapper;
import com.zjxu.educationapp.modules.mapper.SubjectsMapper;
import com.zjxu.educationapp.modules.mapper.TeachHomeworkMapper;
import com.zjxu.educationapp.modules.service.StuHomeworkService;
import com.zjxu.educationapp.modules.vo.StuHomeWorkCorVO;
import com.zjxu.educationapp.modules.vo.StuHomeWorkDetailVO;
import com.zjxu.educationapp.modules.vo.StuHomeWorkSubVO;
import com.zjxu.educationapp.modules.vo.StuHomeWorkVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

import static net.sf.jsqlparser.parser.feature.Feature.update;

/**
 * @author huawei
 * @description 针对表【stu_homework(学生作业信息表)】的数据库操作Service实现
 * @createDate 2025-09-11 20:16:53
 */
@Service
@Slf4j
public class StuHomeworkServiceImpl extends ServiceImpl<StuHomeworkMapper, StuHomework>
        implements StuHomeworkService{
    @Autowired
    private StuHomeworkMapper stuHomeworkMapper;
    @Autowired
    private SubjectsMapper subjectsMapper;
    @Autowired
    private TeachHomeworkMapper teachHomeworkMapper;
    /**
     * 未完成作业的分页查询
     *
     * @param subjectId
     * @param page
     * @param size
     * @return
     */
    @Override
    public Result<IPage<StuHomeWorkVO>> queryUnComplete(Integer subjectId, int page, int size) {
        //获取当前用户ID
        long userId = StpUtil.getLoginIdAsLong();
        //查询条件为userId和未完成
        QueryWrapper<StuHomework> queryWrapper = new QueryWrapper<StuHomework>()
                .eq("user_id", userId)
                .eq("complete_and_correct", 1);
        //判断subjectId是否为null
        if (subjectId!=null){
            //加条件,查该科的未完成
            queryWrapper.eq("subject_id",subjectId);
        }
        //分页查询加排序
        Page<StuHomework> stuHomeworkPage=stuHomeworkMapper
                .selectUnComplete(new Page<>(page, size),userId,subjectId);

        //类型转换
        IPage<StuHomeWorkVO> stuHomeWorkVOIPage = stuHomeworkPage.convert(stuHomework -> {
            StuHomeWorkVO stuHomeWorkVO = new StuHomeWorkVO();
            BeanUtils.copyProperties(stuHomework, stuHomeWorkVO);
            //获取学科ID并查找获取学科名称
            Subjects subject = subjectsMapper.selectById(stuHomework.getSubjectId());
            stuHomeWorkVO.setSubject(subject != null ? subject.getSubjectName() : "未知学科");
            //获取作业ID,查询教师作业表
            TeachHomework teachHomework = teachHomeworkMapper.selectById(stuHomework.getHomeworkId());
            //获取作业名，截止日期，发布时间
            String homeworkName = teachHomework.getHomeworkName();
            Date deadTime = teachHomework.getDeadTime();
            Date sendTime = teachHomework.getSendTime();
            stuHomeWorkVO.setHomeworkName(homeworkName);
            stuHomeWorkVO.setDeadTime(deadTime);
            stuHomeWorkVO.setSendTime(sendTime);
            return stuHomeWorkVO;
        });
        return Result.ok(stuHomeWorkVOIPage);
    }

    /**
     * 删除过期的作业(可批量)
     * @param homeworkIds
     * @return
     */
//
    @Override
    @Transactional
    public Result<?> delOutTime(List<Long> homeworkIds) {
        if (homeworkIds == null || homeworkIds.isEmpty()) {
            log.info("未选择删除的作业");
            return Result.error(ErrorCode.UNSELECTED_FOR_DELETION);
        }

        for (Long homeworkId : homeworkIds) {
            // 分步执行查询，避免复杂SQL中的参数绑定问题
            StuHomework stuHomework = stuHomeworkMapper.selectByUser(homeworkId, StpUtil.getLoginIdAsLong());
            if (stuHomework == null) {
                log.info("作业ID不存在");
                return Result.error(ErrorCode.THE_JOB_DOES_NOT_EXIST);
            }

            // 检查是否已过期
            Long count = teachHomeworkMapper.selectCount(new LambdaQueryWrapper<TeachHomework>()
                    .eq(TeachHomework::getHomeworkId, homeworkId)
                    .le(TeachHomework::getDeadTime, new Date()));

            if (count == null || count <= 0) {
                log.info("作业未过期");
                return Result.error(ErrorCode.THE_ASSIGNMENT_IS_NOT_OVERDUE);
            }

            // 执行更新
            int update = stuHomeworkMapper.update(homeworkId, StpUtil.getLoginIdAsLong());
            if (update == 0) {
                log.info("删除失败");
                return Result.error(ErrorCode.DELETE_FAILED);
            }
        }
        return Result.ok();
    }


    /**
     * 查看该学科已完成但未批改的作业
     * @param subjectId
     * @param page
     * @param size
     * @return
     */
    @Override
    public Result<IPage<StuHomeWorkSubVO>> queryCmplUnCor(int subjectId, int page, int size) {
        long userId = StpUtil.getLoginIdAsLong();
        Page<StuHomework> stuHomeworkPage = stuHomeworkMapper
                .selectPage(new Page<StuHomework>(page, size),new QueryWrapper<StuHomework>()
                        .eq("user_id",userId)
                        .eq("subject_id",subjectId)
                        .eq("complete_and_correct",2)
                        .orderByDesc("submit_time"));
        IPage<StuHomeWorkSubVO> stuHomeWorkVOIPage = stuHomeworkPage.convert(stuHomework -> {
            StuHomeWorkSubVO stuHomeWorkSubVO = new StuHomeWorkSubVO();
            BeanUtils.copyProperties(stuHomework, stuHomeWorkSubVO);
            Subjects subject = subjectsMapper.selectById(subjectId);
            stuHomeWorkSubVO.setSubject(subject != null ? subject.getSubjectName() : "未知学科");
            //获取作业ID,查询教师作业表
            TeachHomework teachHomework = teachHomeworkMapper.selectById(stuHomework.getHomeworkId());
            //获取作业名，截止日期，发布时间
            String homeworkName = teachHomework.getHomeworkName();
            Date deadTime = teachHomework.getDeadTime();
            Date sendTime = teachHomework.getSendTime();
            stuHomeWorkSubVO.setHomeworkName(homeworkName);
            stuHomeWorkSubVO.setDeadTime(deadTime);
            stuHomeWorkSubVO.setSendTime(sendTime);
            return stuHomeWorkSubVO;
        });
        return Result.ok(stuHomeWorkVOIPage);
    }

    /**
     * 查看该学科已完成并已批改的作业
     *
     * @param subjectId
     * @param page
     * @param size
     * @return
     */
    @Override
    public Result<IPage<StuHomeWorkCorVO>> queryCmplCor(int subjectId, int page, int size) {
        long userId = StpUtil.getLoginIdAsLong();
        Page<StuHomework> stuHomeworkPage = stuHomeworkMapper
                .selectPage(new Page<StuHomework>(page, size),new QueryWrapper<StuHomework>()
                        .eq("user_id",userId)
                        .eq("subject_id",subjectId)
                        .eq("complete_and_correct",3)
                        .orderByDesc("correct_time"));
        IPage<StuHomeWorkCorVO> stuHomeWorkCorVOIPage = stuHomeworkPage.convert(stuHomework -> {
            StuHomeWorkCorVO stuHomeWorkCorVO = new StuHomeWorkCorVO();
            BeanUtils.copyProperties(stuHomework, stuHomeWorkCorVO);
            Subjects subject = subjectsMapper.selectById(subjectId);
            stuHomeWorkCorVO.setSubject(subject != null ? subject.getSubjectName() : "未知学科");
            //获取作业ID,查询教师作业表
            TeachHomework teachHomework = teachHomeworkMapper.selectById(stuHomework.getHomeworkId());
            //获取作业名，截止日期，发布时间
            String homeworkName = teachHomework.getHomeworkName();
            Date deadTime = teachHomework.getDeadTime();
            Date sendTime = teachHomework.getSendTime();
            stuHomeWorkCorVO.setHomeworkName(homeworkName);
            stuHomeWorkCorVO.setDeadTime(deadTime);
            stuHomeWorkCorVO.setSendTime(sendTime);
            return stuHomeWorkCorVO;
        });
        return Result.ok(stuHomeWorkCorVOIPage);
    }

    /**
     * 提交作业
     *
     * @param stuHWSubmitDTO
     * @return
     */
    @Override
    public Result<?> submitHomework(StuHWSubmitDTO stuHWSubmitDTO) {
        // 检查是否已过期
        Long count = teachHomeworkMapper.selectCount(new LambdaQueryWrapper<TeachHomework>()
                .eq(TeachHomework::getHomeworkId, stuHWSubmitDTO.getHomeworkId())
                .le(TeachHomework::getDeadTime, new Date()));

        if (count != null || count > 0) {
            log.info("作业已过期");
            return Result.error(ErrorCode.THE_ASSIGNMENT_IS_OVERDUE);
        }

        long userId = StpUtil.getLoginIdAsLong();
        StuHomework stuHomework = stuHomeworkMapper.selectOne(new LambdaQueryWrapper<StuHomework>()
                .eq(StuHomework::getHomeworkId, stuHWSubmitDTO.getHomeworkId())
                .eq(StuHomework::getUserId, userId)
                .eq(StuHomework::getSubjectId, stuHWSubmitDTO.getSubjectId())
                .eq(StuHomework::getLogicalDeletion, 1));
        stuHomework.setCompleteAndCorrect(2);
        stuHomework.setStudentContent(stuHWSubmitDTO.getContent());
        stuHomework.setSubmitTime(new Date());
        stuHomeworkMapper.updateALL(stuHomework);
        return Result.ok();
    }

    /**
     * 获取作业详情
     *
     * @param homeworkId
     * @return
     */
    @Override
    public Result<StuHomeWorkDetailVO> getHomeworkDetail(Long homeworkId) {
        //获取当前学生的作业信息
        StuHomework stuHomework = stuHomeworkMapper.selectOne(new LambdaQueryWrapper<StuHomework>()
                .eq(StuHomework::getHomeworkId, homeworkId)
                .eq(StuHomework::getUserId, StpUtil.getLoginIdAsLong()));
        //获取老师发布的作业信息
        TeachHomework teachHomework = teachHomeworkMapper.selectOne(new LambdaQueryWrapper<TeachHomework>()
                .eq(TeachHomework::getHomeworkId, homeworkId));
        //获取对应科目名
        Subjects subjects = subjectsMapper.selectById(teachHomework.getSubjectId());
        String subjectName = subjects != null ? subjects.getSubjectName() : "未知学科";

        StuHomeWorkDetailVO stuHomeWorkDetailVO = new StuHomeWorkDetailVO();
        stuHomeWorkDetailVO.setSubject(subjectName);
        BeanUtils.copyProperties(teachHomework, stuHomeWorkDetailVO);
        //获取当前学生的作业状态
        Integer completeAndCorrect = stuHomework.getCompleteAndCorrect();
        if (completeAndCorrect==1){
            //未完成
            stuHomeWorkDetailVO.setCompleteAndCorrect(1);
        } else if (completeAndCorrect == 2) {
            //已提交但未批改
            stuHomeWorkDetailVO.setCompleteAndCorrect(2);
            stuHomeWorkDetailVO.setStudentContent(stuHomework.getStudentContent());
            stuHomeWorkDetailVO.setSubmitTime(stuHomework.getSubmitTime());
        }else {
            //已完成并已批改
            stuHomeWorkDetailVO.setCompleteAndCorrect(3);
            stuHomeWorkDetailVO.setStudentContent(stuHomework.getStudentContent());
            stuHomeWorkDetailVO.setScore(stuHomework.getScore());
            stuHomeWorkDetailVO.setTeacherComment(stuHomework.getTeacherComment());
            stuHomeWorkDetailVO.setCorrectTime(stuHomework.getCorrectTime());
            stuHomeWorkDetailVO.setSubmitTime(stuHomework.getSubmitTime());
        }
        return Result.ok(stuHomeWorkDetailVO);
    }

}




