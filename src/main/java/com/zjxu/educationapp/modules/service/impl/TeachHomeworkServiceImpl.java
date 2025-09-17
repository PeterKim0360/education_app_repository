package com.zjxu.educationapp.modules.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjxu.educationapp.common.constant.ErrorCode;
import com.zjxu.educationapp.common.utils.Result;
import com.zjxu.educationapp.modules.dto.TeachCreateHomeworkDTO;
import com.zjxu.educationapp.modules.entity.*;
import com.zjxu.educationapp.modules.mapper.*;
import com.zjxu.educationapp.modules.service.TeachHomeworkService;
import com.zjxu.educationapp.modules.vo.TeachCreateHWDetailVO;
import com.zjxu.educationapp.modules.vo.TeachCreateHWSimpleVO;
import com.zjxu.educationapp.modules.vo.TeachSendHWDetailVO;
import com.zjxu.educationapp.modules.vo.TeachSendHWSimpleVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
* @author huawei
* @description 针对表【teach_homework(教师作业信息表)】的数据库操作Service实现
* @createDate 2025-09-12 00:53:02
*/
@Service
@Slf4j
public class TeachHomeworkServiceImpl extends ServiceImpl<TeachHomeworkMapper, TeachHomework>
    implements TeachHomeworkService{
    @Autowired
    private TeachHomeworkMapper teachHomeworkMapper;
    @Autowired
    private SubjectsMapper subjectsMapper;
    @Autowired
    private SubjectClassMapper subjectClassMapper;
    @Autowired
    private StudentClassMapper studentClassMapper;
    @Autowired
    private StuHomeworkMapper stuHomeworkMapper;
    /**
     * 创建作业
     * @param teachCreateHomeworkDTO
     * @return
     */
    @Override
    public Result<?> createHomework(TeachCreateHomeworkDTO teachCreateHomeworkDTO) {
        //获取当前用户ID
        long userId = StpUtil.getLoginIdAsLong();
        //获取当前时间，为创造时间和更新时间
        Date now = new Date();
        Date updateTime = new Date();
        TeachHomework teachHomework = new TeachHomework();
        BeanUtils.copyProperties(teachCreateHomeworkDTO,teachHomework);
        teachHomework.setCreatedTime(now);
        teachHomework.setUserId(userId);
        teachHomework.setUpdateTime(updateTime);
        teachHomework.setDeadTime(teachCreateHomeworkDTO.getDeadTime());
        teachHomeworkMapper.insert(teachHomework);
        return Result.ok();
    }

    /**
     * 查看已创建的作业
     * @return
     */
    @Override
    public Result<IPage<TeachCreateHWSimpleVO>> queryCreateList(int page, int size) {
        //获取当前用户ID
        long userId = StpUtil.getLoginIdAsLong();
        //根据用户ID查看该用户创建的作业
        Page<TeachHomework> teachHomeworkPage = teachHomeworkMapper.selectPage(new Page<TeachHomework>(page, size), new QueryWrapper<TeachHomework>()
                .eq("user_id", userId)
                .isNull("send_time")
                .orderByDesc("update_time"));
        IPage<TeachCreateHWSimpleVO> teachCreateHWSimpleVOIPage = teachHomeworkPage.convert(teachHomework -> {
            TeachCreateHWSimpleVO teachCreateHWSimpleVO = new TeachCreateHWSimpleVO();
            BeanUtils.copyProperties(teachHomework, teachCreateHWSimpleVO);
            //根据subjectID找对应名称
            Integer subjectId = teachHomework.getSubjectId();
            Subjects subject = subjectsMapper.selectById(subjectId);
            teachCreateHWSimpleVO.setSubject(subject==null?"未知科目":subject.getSubjectName());
            return teachCreateHWSimpleVO;
        });
        return Result.ok(teachCreateHWSimpleVOIPage);
    }

    /**
     * 查看已创建作业详情
     * @return
     */
    @Override
    public Result<TeachCreateHWDetailVO> findCreateHW(Long homeworkId) {
        //获取当前用户ID
        long userId = StpUtil.getLoginIdAsLong();
        TeachHomework teachHomework = teachHomeworkMapper.selectOne(
                new LambdaQueryWrapper<TeachHomework>()
                    .eq(TeachHomework::getHomeworkId,homeworkId)
                    .eq(TeachHomework::getUserId,userId));
        TeachCreateHWDetailVO teachCreateHWDetailVO = new TeachCreateHWDetailVO();
        BeanUtils.copyProperties(teachHomework,teachCreateHWDetailVO);
        //根据subjectID找对应名称
        Integer subjectId = teachHomework.getSubjectId();
        Subjects subject = subjectsMapper.selectById(subjectId);
        teachCreateHWDetailVO.setSubject(subject==null?"未知科目":subject.getSubjectName());
        return Result.ok(teachCreateHWDetailVO);
    }

    /**
     * 编辑作业
     * @param teachCreateHomeworkDTO
     * @return
     */
    @Override
    public Result<?> editCreateHW(TeachCreateHomeworkDTO teachCreateHomeworkDTO) {
        TeachHomework teachHomework = new TeachHomework();
        BeanUtils.copyProperties(teachCreateHomeworkDTO,teachHomework);
        //更新更新时间
        teachHomework.setUpdateTime(new Date());
        teachHomeworkMapper.update(teachHomework,new LambdaQueryWrapper<TeachHomework>()
                .eq(TeachHomework::getHomeworkId,teachCreateHomeworkDTO.getHomeworkId())
                .eq(TeachHomework::getUserId,StpUtil.getLoginIdAsLong()));
        return Result.ok();
    }

    /**
     * 删除已创建的作业 (含批量)
     * @param homeworkIds
     * @return
     */
    @Override
    public Result<?> delCreateHW(List<Long> homeworkIds) {
        if (homeworkIds==null||homeworkIds.isEmpty()){
            log.info("未选择删除的作业");
            return Result.error(ErrorCode.UNSELECTED_FOR_DELETION);
        }
        for (Long homeworkId : homeworkIds) {
            TeachHomework teachHomework = teachHomeworkMapper.selectOne(new LambdaQueryWrapper<TeachHomework>()
                    .eq(TeachHomework::getHomeworkId,homeworkId)
                    .eq(TeachHomework::getUserId,StpUtil.getLoginIdAsLong()));
            teachHomework.setLogicalDeletion(0);
            int update = teachHomeworkMapper.update(teachHomework, new LambdaQueryWrapper<TeachHomework>()
                    .eq(TeachHomework::getHomeworkId,homeworkId)
                    .eq(TeachHomework::getUserId,StpUtil.getLoginIdAsLong()));
            if (update==0){
                log.info("删除失败");
                return Result.error(ErrorCode.DELETE_FAILED);
            }
        }
        return Result.ok();
    }

    /**
     * 发布作业
     * @return
     */
    @Override
    public Result<?> sendHW(Long homeworkId) {
        //发送时间
        Date sendTime = new Date();
        //根据ID找作业
        TeachHomework teachHomework = teachHomeworkMapper.selectOne(new LambdaQueryWrapper<TeachHomework>()
                .eq(TeachHomework::getHomeworkId,homeworkId)
                .eq(TeachHomework::getUserId,StpUtil.getLoginIdAsLong()));
        teachHomework.setSendTime(sendTime);
        teachHomeworkMapper.update(teachHomework,new LambdaQueryWrapper<TeachHomework>()
                .eq(TeachHomework::getHomeworkId,homeworkId)
                .eq(TeachHomework::getUserId,StpUtil.getLoginIdAsLong()));
        //根据对应科目ID找班级ID
        Integer subjectId = teachHomework.getSubjectId();
        //根据班级ID找对应学生ID
        List<Long> classIds=subjectsMapper.selectClass(subjectId);
        for (Long classId : classIds) {
            List<Long> studentIds = studentClassMapper.getStudentId(classId);
            //根据学生ID存入作业信息
            for (Long studentId : studentIds) {
                StuHomework stuHomework = StuHomework.builder()
                        .userId(studentId)
                        .homeworkId(teachHomework.getHomeworkId())
                        .subjectId(teachHomework.getSubjectId())
                        .completeAndCorrect(1)
                        .logicalDeletion(1)
                        .build();
                stuHomeworkMapper.insert(stuHomework);
            }
        }
        return Result.ok();
    }

    /**
     * 删除已发布的作业
     * @param homeworkIds
     * @return
     */
    @Override
    public Result<?> delSendHW(List<Long> homeworkIds) {
        if (homeworkIds==null||homeworkIds.isEmpty()){
            log.info("未选择删除的作业");
            return Result.error(ErrorCode.UNSELECTED_FOR_DELETION);
        }
        //逻辑删除老师的作业记录
        for (Long homeworkId : homeworkIds) {
            log.info("老师作业ID为{}的作业",homeworkId);
            TeachHomework teachHomework = teachHomeworkMapper.selectOne(new LambdaQueryWrapper<TeachHomework>()
                    .eq(TeachHomework::getHomeworkId,homeworkId)
                    .eq(TeachHomework::getUserId,StpUtil.getLoginIdAsLong()));
            teachHomework.setLogicalDeletion(0);
            teachHomeworkMapper.update(teachHomework,new LambdaQueryWrapper<TeachHomework>()
                    .eq(TeachHomework::getHomeworkId,homeworkId)
                    .eq(TeachHomework::getUserId,StpUtil.getLoginIdAsLong()));
        }
        //逻辑删除学生的作业记录
        for (Long homeworkId : homeworkIds) {
            log.info("学生作业ID为{}的作业",homeworkId);
            List<StuHomework> stuHomeworks = stuHomeworkMapper.select(homeworkId,null);
            stuHomeworks.forEach(stuHomework -> {
                stuHomeworkMapper.update(stuHomework.getHomeworkId(),null);
            });
        }
        return Result.ok();
    }

    /**
     * 查询已发布但未截止的作业
     *
     * @param page
     * @param size
     * @return
     */
    @Override
    public Result<IPage<TeachSendHWSimpleVO>> querySendList(int page, int size) {
        //获取当前用户ID
        long userId = StpUtil.getLoginIdAsLong();
        //获取该用户所有发布作业并按发布时间降序排序
        Page<TeachHomework> teachHomeworkPage = teachHomeworkMapper.selectPage(new Page<>(page, size), new LambdaQueryWrapper<TeachHomework>()
                .eq(TeachHomework::getUserId, userId)
                .eq(TeachHomework::getLogicalDeletion, 1)
                .gt(TeachHomework::getDeadTime, new Date())
                .isNotNull(TeachHomework::getSendTime)
                .orderByDesc(TeachHomework::getSendTime));
        IPage<TeachSendHWSimpleVO> teachSendHWSimpleVOIPage = teachHomeworkPage.convert(teachHomework -> {
            TeachSendHWSimpleVO teachSendHWSimpleVO = new TeachSendHWSimpleVO();
            BeanUtils.copyProperties(teachHomework, teachSendHWSimpleVO);
            Subjects subjects = subjectsMapper.selectById(teachHomework.getSubjectId());
            teachSendHWSimpleVO.setSubject(subjects==null?"未知学科":subjects.getSubjectName());
            return teachSendHWSimpleVO;
        });
        return Result.ok(teachSendHWSimpleVOIPage);
    }

    /**
     * 查询已发布作业的详细信息
     *
     * @param homeworkId
     * @return
     */
    @Override
    public Result<TeachSendHWDetailVO> findSendHW(Long homeworkId) {
        TeachHomework teachHomework = teachHomeworkMapper.selectOne(new LambdaQueryWrapper<TeachHomework>()
                .eq(TeachHomework::getHomeworkId, homeworkId)
                .eq(TeachHomework::getUserId, StpUtil.getLoginIdAsLong()));
        TeachSendHWDetailVO teachSendHWDetailVO = new TeachSendHWDetailVO();
        BeanUtils.copyProperties(teachHomework,teachSendHWDetailVO);
        return Result.ok(teachSendHWDetailVO);
    }


}




