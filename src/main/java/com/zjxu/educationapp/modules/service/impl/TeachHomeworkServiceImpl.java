package com.zjxu.educationapp.modules.service.impl;

import cn.dev33.satoken.stp.StpUtil;
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
        TeachHomework teachHomework = teachHomeworkMapper.selectById(homeworkId);
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
        teachHomeworkMapper.update(teachHomework,new QueryWrapper<TeachHomework>()
                .eq("homework_id",teachCreateHomeworkDTO.getHomeworkId()));
        return Result.ok();
    }

    /**
     * 删除已创建的作业 (含批量)
     * @param homeworkId
     * @return
     */
    @Override
    public Result<?> delCreateHW(List<Long> homeworkId) {
        if (homeworkId==null||homeworkId.isEmpty()){
            log.info("未选择删除的作业");
            return Result.error(ErrorCode.UNSELECTED_FOR_DELETION);
        }
        int deleteBatchIds = teachHomeworkMapper.deleteBatchIds(homeworkId);
        if (deleteBatchIds==0){
            log.info("删除失败");
            return Result.error(ErrorCode.DELETE_FAILED);
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
        TeachHomework teachHomework = teachHomeworkMapper.selectById(homeworkId);
        teachHomework.setSendTime(sendTime);
        teachHomeworkMapper.update(teachHomework,new QueryWrapper<TeachHomework>()
                .eq("homework_id",homeworkId));
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
            TeachHomework teachHomework = teachHomeworkMapper.selectById(homeworkId);
            teachHomework.setLogicalDeletion(0);
        }
//        int deleted = teachHomeworkMapper.deleteBatchIds(homeworkIds);
//        if (deleted==0){
//            log.info("老师作业删除失败");
//            return Result.error(ErrorCode.DELETE_FAILED);
//        }
        //逻辑删除学生的作业记录
        for (Long homeworkId : homeworkIds) {
            log.info("学生作业ID为{}的作业",homeworkId);
            StuHomework stuHomework = stuHomeworkMapper.selectById(homeworkId);
            stuHomework.setLogicalDeletion(0);
        }
//        int deleted1 = stuHomeworkMapper.deleteBatchIds(homeworkIds);
//        if (deleted1==0){
//            log.info("学生作业删除失败");
//            return Result.error(ErrorCode.DELETE_FAILED);
//        }
        return Result.ok();
    }


}




