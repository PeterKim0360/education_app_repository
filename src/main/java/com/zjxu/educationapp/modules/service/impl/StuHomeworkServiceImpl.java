package com.zjxu.educationapp.modules.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjxu.educationapp.common.constant.ErrorCode;
import com.zjxu.educationapp.common.utils.Result;
import com.zjxu.educationapp.modules.entity.StuHomework;
import com.zjxu.educationapp.modules.entity.Subjects;
import com.zjxu.educationapp.modules.entity.TeachHomework;
import com.zjxu.educationapp.modules.mapper.StuHomeworkMapper;
import com.zjxu.educationapp.modules.mapper.SubjectsMapper;
import com.zjxu.educationapp.modules.mapper.TeachHomeworkMapper;
import com.zjxu.educationapp.modules.service.StuHomeworkService;
import com.zjxu.educationapp.modules.vo.StuHomeWorkCorVO;
import com.zjxu.educationapp.modules.vo.StuHomeWorkSubVO;
import com.zjxu.educationapp.modules.vo.StuHomeWorkVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

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
    @Override
    public Result<?> delOutTime(List<Long> homeworkIds) {
        if(homeworkIds.isEmpty()||homeworkIds==null){
            log.info("未选择删除的作业");
            return Result.error(ErrorCode.UNSELECTED_FOR_DELETION);
        }
        long userId = StpUtil.getLoginIdAsLong();
        int deleted = stuHomeworkMapper.delete(new QueryWrapper<StuHomework>()
                .in("homework_id", homeworkIds)
                .eq("user_id", userId)
                // 子查询判断作业是否过期
                .apply("homework_id IN (SELECT homework_id FROM teach_homework WHERE dead_time <= NOW())"));
        if (deleted==0){
            log.info("作业ID不存在或者都未过期");
            return Result.error(ErrorCode.DOES_NOT_EXIST_OR_HAS_NOT_EXPIRED);
        }
        log.info("有{}个不能删除，因为还未截止",homeworkIds.size()-deleted);
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

}




