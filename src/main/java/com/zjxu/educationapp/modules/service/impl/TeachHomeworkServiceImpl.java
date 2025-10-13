package com.zjxu.educationapp.modules.service.impl;

import cn.dev33.satoken.stp.StpUtil;

import com.alibaba.fastjson.JSONException;
import com.volcengine.ark.runtime.model.completion.chat.ChatCompletionContentPart;
import com.volcengine.ark.runtime.model.completion.chat.ChatCompletionRequest;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessage;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessageRole;
import com.volcengine.ark.runtime.service.ArkService;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjxu.educationapp.common.config.DashScopeConfig;
import com.zjxu.educationapp.common.config.DoubaoConfig;
import com.zjxu.educationapp.common.constant.ErrorCode;
import com.zjxu.educationapp.common.utils.AliOSSUtil;
import com.zjxu.educationapp.common.utils.Result;
import com.zjxu.educationapp.modules.dto.HomeworkSubmissionDTO;
import com.zjxu.educationapp.modules.dto.StuHWSubmitDTO;
import com.zjxu.educationapp.modules.dto.TeachCreateHomeworkDTO;
import com.zjxu.educationapp.modules.dto.TeachEditHomeworkDTO;
import com.zjxu.educationapp.modules.entity.*;
import com.zjxu.educationapp.modules.mapper.*;
import com.zjxu.educationapp.modules.service.TeachHomeworkService;
import com.zjxu.educationapp.modules.vo.*;
import lombok.extern.slf4j.Slf4j;
import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
* @author huawei
* @description 针对表【teach_homework(教师作业信息表)】的数据库操作Service实现
* @createDate 2025-09-12 00:53:02
*/
@Service
@Slf4j
public class TeachHomeworkServiceImpl extends ServiceImpl<TeachHomeworkMapper, TeachHomework>
    implements TeachHomeworkService {
    @Autowired
    private SubjectClassTeachMapper subjectClassTeachMapper;
    @Autowired
    private TeachHomeworkMapper teachHomeworkMapper;
    @Autowired
    private SubjectsMapper subjectsMapper;
    @Autowired
    private StudentClassMapper studentClassMapper;
    @Autowired
    private StuHomeworkMapper stuHomeworkMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private DashScopeConfig dashScopeConfig;

    @Autowired
    private DoubaoConfig doubaoConfig;

    @Value("${ai.doubao.model-name:doubao-seed-1-6-vision-250815}")
    private String doubaoModelName;

    @Value("${doubao.base-url:https://ark.cn-beijing.volces.com/api/v3}")
    private String doubaoBaseUrl;


    private final String SYSTEMPROMPT = "你是一位专业的教育工作者和作业设计专家。请根据用户提供的要求生成一份结构完整的作业，包含以下信息：\n" +
            "1. 作业标题（homeworkName）\n" +
            "2. 作业内容（content）\n" +
            "请严格按照以下JSON格式输出，不要包含其他内容：\n" +
            "{\n" +
            "  \"homeworkName\": \"作业标题\",\n" +
            "  \"content\": \"作业详细内容\",\n" +
            "}";

    /**
     * 创建作业
     *
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
        BeanUtils.copyProperties(teachCreateHomeworkDTO, teachHomework);
        teachHomework.setCreatedTime(now);
        teachHomework.setUserId(userId);
        teachHomework.setUpdateTime(updateTime);
        teachHomework.setDeadTime(teachCreateHomeworkDTO.getDeadTime());
        if (teachCreateHomeworkDTO.getImageUrls() != null) {
            teachHomework.setImageUrls(JSON.toJSONString(teachCreateHomeworkDTO.getImageUrls()));
        }
        teachHomeworkMapper.insert(teachHomework);
        return Result.ok();
    }

    /**
     * 查看已创建的作业
     *
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
                .orderByDesc("update_time")
                .eq("logical_deletion", 1));
        IPage<TeachCreateHWSimpleVO> teachCreateHWSimpleVOIPage = teachHomeworkPage.convert(teachHomework -> {
            TeachCreateHWSimpleVO teachCreateHWSimpleVO = new TeachCreateHWSimpleVO();
            BeanUtils.copyProperties(teachHomework, teachCreateHWSimpleVO);
            //根据subjectID找对应名称
            Integer subjectId = teachHomework.getSubjectId();
            Subjects subject = subjectsMapper.selectById(subjectId);
            teachCreateHWSimpleVO.setSubject(subject == null ? "未知科目" : subject.getSubjectName());
            return teachCreateHWSimpleVO;
        });
        return Result.ok(teachCreateHWSimpleVOIPage);
    }

    /**
     * 查看已创建作业详情
     *
     * @return
     */
    @Override
    public Result<TeachCreateHWDetailVO> findCreateHW(Long homeworkId) {
        //获取当前用户ID
        long userId = StpUtil.getLoginIdAsLong();
        TeachHomework teachHomework = teachHomeworkMapper.selectOne(
                new LambdaQueryWrapper<TeachHomework>()
                        .eq(TeachHomework::getHomeworkId, homeworkId)
                        .eq(TeachHomework::getUserId, userId)
                        .isNull(TeachHomework::getSendTime));
        TeachCreateHWDetailVO teachCreateHWDetailVO = new TeachCreateHWDetailVO();
        BeanUtils.copyProperties(teachHomework, teachCreateHWDetailVO);
        //根据subjectID找对应名称
        Integer subjectId = teachHomework.getSubjectId();
        teachCreateHWDetailVO.setSubjectId(subjectId);
        Subjects subject = subjectsMapper.selectById(subjectId);
        teachCreateHWDetailVO.setSubject(subject == null ? "未知科目" : subject.getSubjectName());
        teachCreateHWDetailVO.setImageUrls(JSON.parseArray(teachHomework.getImageUrls(), String.class));
        return Result.ok(teachCreateHWDetailVO);
    }

    /**
     * 编辑作业
     *
     * @param teachEditHomeworkDTO
     * @return
     */
    @Override
    public Result<?> editCreateHW(TeachEditHomeworkDTO teachEditHomeworkDTO) {
        TeachHomework teachHomework = new TeachHomework();
        BeanUtils.copyProperties(teachEditHomeworkDTO, teachHomework);
//        Subjects subjects = subjectsMapper.selectOne(new LambdaQueryWrapper<Subjects>()
//                .eq(Subjects::getSubjectName, teachEditHomeworkDTO.getSubjectName()));
//        Integer subjectId = subjects.getSubjectId();
//        teachHomework.setSubjectId(subjectId);
        teachHomework.setImageUrls(teachEditHomeworkDTO.getImageUrls() == null ? "" : JSON.toJSONString(teachEditHomeworkDTO.getImageUrls()));
        //更新更新时间
        teachHomework.setUpdateTime(new Date());
        teachHomeworkMapper.update(teachHomework, new LambdaQueryWrapper<TeachHomework>()
                .eq(TeachHomework::getHomeworkId, teachEditHomeworkDTO.getHomeworkId())
                .eq(TeachHomework::getUserId, StpUtil.getLoginIdAsLong()));
        return Result.ok();
    }

    /**
     * 删除已创建的作业 (含批量)
     *
     * @param homeworkIds
     * @return
     */
    @Override
    public Result<?> delCreateHW(List<Long> homeworkIds) {
        if (homeworkIds == null || homeworkIds.isEmpty()) {
            log.info("未选择删除的作业");
            return Result.error(ErrorCode.UNSELECTED_FOR_DELETION);
        }
        for (Long homeworkId : homeworkIds) {
            TeachHomework teachHomework = teachHomeworkMapper.selectOne(new LambdaQueryWrapper<TeachHomework>()
                    .eq(TeachHomework::getHomeworkId, homeworkId)
                    .eq(TeachHomework::getUserId, StpUtil.getLoginIdAsLong()));
            teachHomework.setLogicalDeletion(0);
            int update = teachHomeworkMapper.update(teachHomework, new LambdaQueryWrapper<TeachHomework>()
                    .eq(TeachHomework::getHomeworkId, homeworkId)
                    .eq(TeachHomework::getUserId, StpUtil.getLoginIdAsLong()));
            if (update == 0) {
                log.info("删除失败");
                return Result.error(ErrorCode.DELETE_FAILED);
            }
        }
        return Result.ok();
    }

    /**
     * 发布作业
     *
     * @return
     */
    @Override
    public Result<?> sendHW(Long homeworkId) {
        //发送时间
        Date sendTime = new Date();
        //根据ID找作业
        TeachHomework teachHomework = teachHomeworkMapper.selectOne(new LambdaQueryWrapper<TeachHomework>()
                .eq(TeachHomework::getHomeworkId, homeworkId)
                .eq(TeachHomework::getUserId, StpUtil.getLoginIdAsLong()));
        teachHomework.setSendTime(sendTime);
        teachHomeworkMapper.update(teachHomework, new LambdaQueryWrapper<TeachHomework>()
                .eq(TeachHomework::getHomeworkId, homeworkId)
                .eq(TeachHomework::getUserId, StpUtil.getLoginIdAsLong()));
        //根据对应科目ID和教师ID查询对应信息
        Integer subjectId = teachHomework.getSubjectId();
        List<SubjectClassTeach> teaches = subjectClassTeachMapper.selectList(new LambdaQueryWrapper<SubjectClassTeach>()
                .eq(SubjectClassTeach::getSubjectId, subjectId)
                .eq(SubjectClassTeach::getTeachId, StpUtil.getLoginIdAsLong()));
        for (SubjectClassTeach teach : teaches) {
            Long classId = teach.getClassId();
            //根据班级ID找对应学生ID
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
     *
     * @param homeworkIds
     * @return
     */
    @Override
    public Result<?> delSendHW(List<Long> homeworkIds) {
        if (homeworkIds == null || homeworkIds.isEmpty()) {
            log.info("未选择删除的作业");
            return Result.error(ErrorCode.UNSELECTED_FOR_DELETION);
        }
        //逻辑删除老师的作业记录
        for (Long homeworkId : homeworkIds) {
            log.info("老师作业ID为{}的作业", homeworkId);
            TeachHomework teachHomework = teachHomeworkMapper.selectOne(new LambdaQueryWrapper<TeachHomework>()
                    .eq(TeachHomework::getHomeworkId, homeworkId)
                    .eq(TeachHomework::getUserId, StpUtil.getLoginIdAsLong()));
            teachHomework.setLogicalDeletion(0);
            teachHomeworkMapper.update(teachHomework, new LambdaQueryWrapper<TeachHomework>()
                    .eq(TeachHomework::getHomeworkId, homeworkId)
                    .eq(TeachHomework::getUserId, StpUtil.getLoginIdAsLong()));
        }
        //逻辑删除学生的作业记录
        for (Long homeworkId : homeworkIds) {
            log.info("学生作业ID为{}的作业", homeworkId);
            List<StuHomework> stuHomeworks = stuHomeworkMapper.select(homeworkId, null);
            stuHomeworks.forEach(stuHomework -> {
                stuHomeworkMapper.update(stuHomework.getHomeworkId(), null);
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
            teachSendHWSimpleVO.setSubject(subjects == null ? "未知学科" : subjects.getSubjectName());
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
        BeanUtils.copyProperties(teachHomework, teachSendHWDetailVO);
        Subjects subjects = subjectsMapper.selectById(teachHomework.getSubjectId());
        teachSendHWDetailVO.setSubject(subjects == null ? "未知学科" : subjects.getSubjectName());
        teachSendHWDetailVO.setImageUrls(teachHomework.getImageUrls() == null ? List.of() : JSON.parseArray(teachHomework.getImageUrls(), String.class));
        return Result.ok(teachSendHWDetailVO);
    }


    /**
     * 查看待批改作业详情
     *
     * @param subjectId
     * @param homeworkId
     * @return
     */
    @Override
    public Result<List<HomeworkSubmissionVO>> queryUnCorDetailList(Integer subjectId, Long homeworkId) {
        //根据科目ID，作业ID，是否提交，是否存在，批改状态查询作业信息，根据提交时间降序排序
        List<StuHomework> stuHomeworks = stuHomeworkMapper.selectList(new LambdaQueryWrapper<StuHomework>()
                .eq(StuHomework::getSubjectId, subjectId)
                .eq(StuHomework::getHomeworkId, homeworkId)
                .eq(StuHomework::getCompleteAndCorrect, 2)
                .eq(StuHomework::getLogicalDeletion, 1)
                .orderByDesc(StuHomework::getSubmitTime));
        log.info("待批改作业为{}", stuHomeworks);
        if (stuHomeworks.isEmpty()){
            TeachHomework teachHomework = teachHomeworkMapper.selectOne(new LambdaQueryWrapper<TeachHomework>()
                    .eq(TeachHomework::getHomeworkId, homeworkId)
                    .eq(TeachHomework::getUserId, StpUtil.getLoginIdAsLong()));
            teachHomework.setCorrect(1);
            teachHomeworkMapper.updateById(teachHomework);
            return Result.ok(List.of());
        }
        List<HomeworkSubmissionVO> homeworkSubmissionList = new ArrayList<>();
        for (StuHomework stuHomework : stuHomeworks) {
            HomeworkSubmissionVO homeworkSubmissionVO = new HomeworkSubmissionVO();
            homeworkSubmissionVO.setHomeworkId(stuHomework.getHomeworkId());
            homeworkSubmissionVO.setStudentId(stuHomework.getUserId());
            homeworkSubmissionVO.setStudentName(userMapper.selectById(stuHomework.getUserId()).getUserName());
            homeworkSubmissionVO.setSubmitContent(JSON.parseArray(stuHomework.getStudentContent(), String.class));
            homeworkSubmissionVO.setSubmitTime(stuHomework.getSubmitTime());
            homeworkSubmissionList.add(homeworkSubmissionVO);
        }
        return Result.ok(homeworkSubmissionList);
    }

    /**
     * 查询所有待批改作业
     *
     * @return
     */
    @Override
    public Result<List<TeachUnCorrectSimHWVO>> queryUnCorSimList() {
        long teacherId = StpUtil.getLoginIdAsLong();
        //教师ID，截止日期，是否批改，是否存在
        List<TeachHomework> homeworks = teachHomeworkMapper.selectList(new LambdaQueryWrapper<TeachHomework>()
                .eq(TeachHomework::getUserId, teacherId)
                .eq(TeachHomework::getLogicalDeletion, 1)
                .lt(TeachHomework::getDeadTime, new Date())
                .eq(TeachHomework::getCorrect, 0)
                .or()
                .eq(TeachHomework::getCorrect, 2));
        if (homeworks == null){
            return Result.ok(List.of());
        }
        List<TeachUnCorrectSimHWVO> teachUnCorrectSimHWVOS = homeworks.stream().map(homework -> {
            TeachUnCorrectSimHWVO teachUnCorrectSimHWVO = new TeachUnCorrectSimHWVO();
            teachUnCorrectSimHWVO.setHomeworkId(homework.getHomeworkId());
            teachUnCorrectSimHWVO.setHomeworkName(homework.getHomeworkName());
            teachUnCorrectSimHWVO.setSubjectId(homework.getSubjectId());
            teachUnCorrectSimHWVO.setSubjectName(subjectsMapper.selectById(homework.getSubjectId()).getSubjectName());
            return teachUnCorrectSimHWVO;
        }).toList();
        return Result.ok(teachUnCorrectSimHWVOS);
    }

    /**
     * 批改作业
     *
     * @param homeworkSubmissionDTO
     * @return
     */
    @Override
    public Result<?> correctHW(HomeworkSubmissionDTO homeworkSubmissionDTO) {
        // 根据作业ID，更新老师作业状态
        TeachHomework teachHomework = teachHomeworkMapper.selectOne(new LambdaQueryWrapper<TeachHomework>()
                .eq(TeachHomework::getHomeworkId, homeworkSubmissionDTO.getHomeworkId())
                .eq(TeachHomework::getUserId, StpUtil.getLoginIdAsLong())
                .eq(TeachHomework::getCorrect,0));
        if (teachHomework!=null){
            teachHomework.setCorrect(2);
            teachHomeworkMapper.updateById(teachHomework);
        }
        //根据学生ID，作业ID，是否存在查询作业信息
        StuHomework stuHomework = stuHomeworkMapper.selectOne(new LambdaQueryWrapper<StuHomework>()
                .eq(StuHomework::getUserId, homeworkSubmissionDTO.getStudentId())
                .eq(StuHomework::getHomeworkId, homeworkSubmissionDTO.getHomeworkId())
                .eq(StuHomework::getLogicalDeletion, 1));
        stuHomework.setCompleteAndCorrect(3);
        stuHomework.setScore(homeworkSubmissionDTO.getScore());
        stuHomework.setTeacherComment(homeworkSubmissionDTO.getTeacherComment());
        stuHomework.setCorrectTime(new Date());
        //更新作业信息
        stuHomeworkMapper.updateALL(stuHomework);
        return Result.ok();
    }

    /**
     * 查询已批改作业
     *
     * @return
     */
    @Override
    public Result<List<CorrectVO>> queryCorrectList(Integer subjectId, Long homeworkId) {
        //根据科目ID，作业ID，是否提交，是否存在，批改状态查询作业信息，根据批改时间降序排序
        List<StuHomework> stuHomeworks = stuHomeworkMapper.selectList(new LambdaQueryWrapper<StuHomework>()
                .eq(StuHomework::getSubjectId, subjectId)
                .eq(StuHomework::getHomeworkId, homeworkId)
                .eq(StuHomework::getCompleteAndCorrect, 3)
                .eq(StuHomework::getLogicalDeletion, 1)
                .orderByDesc(StuHomework::getCorrectTime));
        if (stuHomeworks== null){
            return Result.ok(List.of());
        }
        List<CorrectVO> correctList = new ArrayList<>();
        for (StuHomework stuHomework : stuHomeworks) {
            CorrectVO correctVO = new CorrectVO();
            correctVO.setHomeworkId(stuHomework.getHomeworkId());
            correctVO.setStudentId(stuHomework.getUserId());
            correctVO.setStudentName(userMapper.selectById(stuHomework.getUserId()).getUserName());
            correctVO.setTeacherComment(stuHomework.getTeacherComment());
            correctVO.setScore(stuHomework.getScore());
            correctList.add(correctVO);
        }
        return Result.ok(correctList);
    }

    /**
     * 通过AI生成作业 （截止日期由老师指定）
     *
     * @param msg
     * @return
     */
    @Override
    public Result<?> createHWByAI(String msg, Integer subjectId) {
        try {
            //获取当前用户ID
            long userId = StpUtil.getLoginIdAsLong();
            Date now = new Date();
            // 用户提示词
            String userPrompt = "请根据以下要求生成作业：" + msg;
            // 调用现有的AIGC服务
            AIGCService aigcService = new AIGCService(dashScopeConfig);
            String aiResponse = aigcService.callAI(SYSTEMPROMPT, userPrompt);
            // 解析AI返回的JSON
            AIHomeworkResponse aiHomework = JSON.parseObject(aiResponse, AIHomeworkResponse.class);

            TeachHomework teachHomework = new TeachHomework();
            teachHomework.setHomeworkName(aiHomework.getHomeworkName());
            teachHomework.setHomeworkContent(aiHomework.getContent());
            teachHomework.setUserId(userId);
            teachHomework.setCreatedTime(now);
            teachHomework.setUpdateTime(now);
            teachHomework.setSubjectId(subjectId);
            teachHomeworkMapper.insert(teachHomework);
        } catch (Exception e) {
            log.error("AI生成作业失败", e);
            return Result.error("AI生成作业失败：" + e.getMessage());
        }
        return Result.ok();
    }


    /**
     * 使用豆包大模型视觉识别批改作业（不生成图片）
     */
    @Override
    public Result<?> correctHWByAI(StuHWSubmitDTO stuHWSubmitDTO) {
        Long homeworkId = stuHWSubmitDTO.getHomeworkId();
        Long studentId = stuHWSubmitDTO.getStudentId();
        // 根据作业ID，更新老师作业状态
        TeachHomework teachHomework1 = teachHomeworkMapper.selectOne(new LambdaQueryWrapper<TeachHomework>()
                .eq(TeachHomework::getHomeworkId, stuHWSubmitDTO.getHomeworkId())
                .eq(TeachHomework::getUserId, StpUtil.getLoginIdAsLong())
                .eq(TeachHomework::getCorrect,0));
        if (teachHomework1!=null){
            teachHomework1.setCorrect(2);
            teachHomeworkMapper.updateById(teachHomework1);
        }
        try {
            // 1. 权限校验与基础数据查询
            long teacherId = StpUtil.getLoginIdAsLong();
            Date now = new Date();

            // 查询作业信息
            TeachHomework teachHomework = teachHomeworkMapper.selectOne(
                    new LambdaQueryWrapper<TeachHomework>()
                            .eq(TeachHomework::getHomeworkId, homeworkId)
                            .eq(TeachHomework::getUserId, teacherId)
            );
            if (teachHomework == null) {
                return Result.error("作业不存在或无权限批改");
            }

            // 查询学生提交记录
            StuHomework stuHomework = stuHomeworkMapper.selectOne(
                    new LambdaQueryWrapper<StuHomework>()
                            .eq(StuHomework::getHomeworkId, homeworkId)
                            .eq(StuHomework::getUserId, studentId)
                            .eq(StuHomework::getLogicalDeletion, 1)
            );
            if (stuHomework == null) {
                return Result.error("学生作业记录不存在");
            }

            // 2. 解析学生提交的图片URL列表
            List<String> studentImageUrls = JSON.parseArray(
                    stuHomework.getStudentContent(), String.class
            );

            if (studentImageUrls == null || studentImageUrls.isEmpty()) {
                return Result.error("未找到有效的作业图片");
            }

            // 3. 简化系统提示词
            String systemPrompt = "你是一位专业的教育工作者和作业批改专家。请根据以下要求对学生作业进行批改：\n" +
                    "1. 分析学生作业图片内容\n" +
                    "2. 根据作业要求判断答案是否正确\n" +
                    "3. 给出评分(0-100分)\n" +
                    "4. 提供不用太详细但需要有关键词的评语，包括优点、不足和改进建议，10个字以内\n" +
                    "请严格按照以下JSON格式返回结果，不要包含其他内容：\n" +
                    "{\n" +
                    "  \"score\": 90,\n" +
                    "  \"comment\": \"不用太详细但需要有关键词的评语内容，不要使用反斜杠开头的数学符号命令，如\\limits, \\times等，用文字描述或使用Unicode符号\",\n" +
                    "  \"correctedImageUrl\": null\n" +
                    "}";

            StringBuilder userPrompt = new StringBuilder();
            userPrompt.append("作业要求：").append(
                    Optional.ofNullable(teachHomework.getHomeworkContent()).orElse("无具体要求")
            ).append("\n请根据以上作业要求批改学生提交的作业图片。");

            // 4. 调用豆包大模型服务
            DoubaoHomeworkCorrectionResult correctionResult = callDoubaoModel(systemPrompt, userPrompt.toString(), studentImageUrls);

            // 5. 更新作业状态与结果
            stuHomework.setCompleteAndCorrect(3); // 标记为已批改
            stuHomework.setScore(correctionResult.getScore());
            stuHomework.setTeacherComment(correctionResult.getComment());
            stuHomework.setCorrectTime(now);
            stuHomeworkMapper.updateALL(stuHomework);

            // 6. 构建返回结果（不处理图片）
            CorrectionResultWithImageUrl resultVO = new CorrectionResultWithImageUrl();
            resultVO.setScore(correctionResult.getScore());
            resultVO.setTeacherComment(correctionResult.getComment());
            return Result.ok(resultVO);

        } catch (Exception e) {
            log.error("豆包大模型作业批改流程异常 - 作业ID: {}, 学生ID: {}", homeworkId, studentId, e);
            return Result.error("系统异常: " + e.getMessage());
        }
    }

    /**
     * 查询已批改作业列表
     */
    @Override
    public Result<List<CorrectSimpleVO>> queryCorSimList() {
        long teacherId = StpUtil.getLoginIdAsLong();
        List<TeachHomework> homeworks = teachHomeworkMapper.selectList(new LambdaQueryWrapper<TeachHomework>()
                .eq(TeachHomework::getUserId, teacherId)
                .eq(TeachHomework::getLogicalDeletion, 1)
                .and(wrapper -> wrapper.eq(TeachHomework::getCorrect, 2)
                        .or()
                        .eq(TeachHomework::getCorrect, 1)));
        log.info("查询已批改作业列表 - 教师ID: {}", teacherId);
        log.info("查询已批改作业列表 - 结果: {}", homeworks);
        if (homeworks.isEmpty()){
            return Result.ok(List.of());
        }
        List<CorrectSimpleVO> correctSimpleVOS = homeworks.stream().map(homework -> {
            CorrectSimpleVO correctSimpleVO = new CorrectSimpleVO();
            correctSimpleVO.setHomeworkId(homework.getHomeworkId());
            correctSimpleVO.setHomeworkName(homework.getHomeworkName());
            correctSimpleVO.setSubjectId(homework.getSubjectId());
            correctSimpleVO.setSubjectName(subjectsMapper.selectById(homework.getSubjectId()).getSubjectName());
            return correctSimpleVO;
        }).toList();
        return Result.ok(correctSimpleVOS);
    }


    /**
     * 调用豆包大模型进行作业批改
     */
    private DoubaoHomeworkCorrectionResult callDoubaoModel(String systemPrompt, String userPrompt, List<String> imageUrls) throws Exception {
        // 初始化豆包服务
        ConnectionPool connectionPool = new ConnectionPool(5, 1, TimeUnit.SECONDS);
        Dispatcher dispatcher = new Dispatcher();
        ArkService service = ArkService.builder()
                .dispatcher(dispatcher)
                .connectionPool(connectionPool)
                .baseUrl(doubaoBaseUrl)
                .apiKey(doubaoConfig.getApiKey())
                .build();

        try {
            final List<ChatMessage> messages = new ArrayList<>();

            // 添加系统提示
            final ChatMessage systemMessage = ChatMessage.builder()
                    .role(ChatMessageRole.SYSTEM)
                    .content(systemPrompt)
                    .build();
            messages.add(systemMessage);

            // 构造多模态消息内容
            final List<ChatCompletionContentPart> multiParts = new ArrayList<>();

            // 添加图片内容
            for (String imageUrl : imageUrls) {
                if (imageUrl != null && !imageUrl.trim().isEmpty()) {
                    multiParts.add(ChatCompletionContentPart.builder()
                            .type("image_url")
                            .imageUrl(new ChatCompletionContentPart.ChatCompletionContentPartImageURL(imageUrl))
                            .build());
                }
            }

            // 添加文本提示
            multiParts.add(ChatCompletionContentPart.builder()
                    .type("text")
                    .text(userPrompt)
                    .build());

            final ChatMessage userMessage = ChatMessage.builder()
                    .role(ChatMessageRole.USER)
                    .multiContent(multiParts)
                    .build();
            messages.add(userMessage);

            // 构造请求
            ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                    .model(doubaoModelName)
                    .messages(messages)
                    .build();

            // 发起请求并获取结果
            // 在 callDoubaoModel 方法中，在JSON解析前添加预处理
            String aiResponse = (String) service.createChatCompletion(chatCompletionRequest)
                    .getChoices().get(0).getMessage().getContent();

            // 预处理AI响应，修复潜在的转义问题
            if (aiResponse != null) {
                // 先进行JSON转义处理
                aiResponse = aiResponse.replace("\\\"", "\"")  // 修复双引号转义
                        .replace("\\\\", "\\");  // 修复双重反斜杠

                // 清理可能导致JSON解析错误的转义字符
                aiResponse = aiResponse.replace("\\geq", "≥")
                        .replace("\\leq", "≤")
                        .replace("\\limits", "lim")
                        .replace("\\times", "×")
                        .replace("\\to", "→")
                        .replace("\\infty", "∞")
                        .replace("\\int", "∫")
                        .replace("\\sum", "∑")
                        .replace("\\sqrt", "√")
                        .replace("\\left", "")
                        .replace("\\right", "")
                        .replace("\\(", "(")
                        .replace("\\)", ")");
            }



            // 解析AI返回的JSON结果
            try {
                return JSON.parseObject(aiResponse, DoubaoHomeworkCorrectionResult.class);
            } catch (JSONException e) {
                log.warn("首次JSON解析失败，原始响应: {}", aiResponse);
                // 尝试更彻底的清理
                if (aiResponse != null) {
                    // 使用正则表达式移除所有LaTeX命令
                    aiResponse = aiResponse.replaceAll("\\\\[a-zA-Z]+", ""); // 移除所有\开头的字母命令
                    aiResponse = aiResponse.replaceAll("\\s+", " ").trim();  // 规范化空格

                    try {
                        DoubaoHomeworkCorrectionResult result = JSON.parseObject(aiResponse, DoubaoHomeworkCorrectionResult.class);
                        log.info("清理后解析成功");
                        return result;
                    } catch (JSONException e2) {
                        log.error("清理后仍然无法解析JSON，原始响应: {}", aiResponse);
                    }
                }
                throw e;
            }



        } finally {
            service.shutdownExecutor();
        }
    }



    /**
     * 豆包大模型作业批改结果类
     */
    public static class DoubaoHomeworkCorrectionResult {
        private Double score;
        private String comment;
        private String correctedImageUrl;

        // Getters and setters
        public Double getScore() { return score; }
        public void setScore(Double score) { this.score = score; }

        public String getComment() { return comment; }
        public void setComment(String comment) { this.comment = comment; }

        public String getCorrectedImageUrl() { return correctedImageUrl; }
        public void setCorrectedImageUrl(String correctedImageUrl) { this.correctedImageUrl = correctedImageUrl; }
    }



//    /**
//     * AI 批改作业 (修改版本 - 下载图片后传递给AI)
//     */
//    @Override
//    public Result<?> correctHWByAI(StuHWSubmitDTO stuHWSubmitDTO) {
//        Long homeworkId = stuHWSubmitDTO.getHomeworkId();
//        Long studentId = stuHWSubmitDTO.getStudentId();
//
//        try {
//            // 1. 权限校验与基础数据查询
//            long teacherId = StpUtil.getLoginIdAsLong();
//            Date now = new Date();
//
//            // 查询作业信息
//            TeachHomework teachHomework = teachHomeworkMapper.selectOne(
//                    new LambdaQueryWrapper<TeachHomework>()
//                            .eq(TeachHomework::getHomeworkId, homeworkId)
//                            .eq(TeachHomework::getUserId, teacherId)
//            );
//            if (teachHomework == null) {
//                return Result.error("作业不存在或无权限批改");
//            }
//
//            // 查询学生提交记录
//            StuHomework stuHomework = stuHomeworkMapper.selectOne(
//                    new LambdaQueryWrapper<StuHomework>()
//                            .eq(StuHomework::getHomeworkId, homeworkId)
//                            .eq(StuHomework::getUserId, studentId)
//                            .eq(StuHomework::getLogicalDeletion, 1)
//            );
//            if (stuHomework == null) {
//                return Result.error("学生作业记录不存在");
//            }
//
//            // 2. 下载作业图片（转换为byte[]）
//            List<byte[]> imageDatas = new ArrayList<>();
//            List<String> imageFormats = new ArrayList<>();
//
//            // 解析学生提交的图片URL列表
//            List<String> studentImageUrls = JSON.parseArray(
//                    stuHomework.getStudentContent(), String.class
//            );
//
//            if (studentImageUrls != null && !studentImageUrls.isEmpty()) {
//                for (String imgUrl : studentImageUrls) {
//                    if (imgUrl != null && !imgUrl.trim().isEmpty()) {
//                        try {
//                            // 下载图片数据
//                            byte[] imageData = downloadImageFromUrl(imgUrl);
//                            imageDatas.add(imageData);
//                            imageFormats.add(getImageFormatFromUrl(imgUrl));
//                        } catch (Exception e) {
//                            log.error("下载图片失败: {}", imgUrl, e);
//                            return Result.error("下载图片失败: " + e.getMessage());
//                        }
//                    }
//                }
//            }
//
//            // 校验是否有有效的图片数据
//            if (imageDatas.isEmpty()) {
//                return Result.error("未找到有效的作业图片");
//            }
//
//            // 3. 调用AI批改服务
//            // 3.1 构建提示词
//            String systemPrompt = "你是专业教师，负责批改作业。请分析图片内容，返回：\n" +
//                    "1. 评分(score，0-100分)\n" +
//                    "2. 评语(comment，含优点、不足和建议)\n" +
//                    "3. 批改后图片URL(correctedImageUrl，如果有)\n" +
//                    "严格按JSON格式输出，不包含其他内容：\n" +
//                    "{\"score\":90,\"comment\":\"评语内容\",\"correctedImageUrl\":\"图片URL\"}";
//
//            StringBuilder userPrompt = new StringBuilder();
//            userPrompt.append("作业要求：").append(
//                    Optional.ofNullable(teachHomework.getHomeworkContent()).orElse("无")
//            ).append("\n请基于上述要求批改提交的作业图片，并返回批改后的图片。");
//
//            // 3.2 调用AI服务
//            AIHomeworkCorrector corrector = new AIHomeworkCorrector(
//                    dashScopeApiKey, dashScopeSdkVersion
//            );
//
//            AIHomeworkCorrector.CorrectionResultWithImageUrl correctionResult =
//                    (AIHomeworkCorrector.CorrectionResultWithImageUrl) corrector.correctHomeworkAndReturnImageUrl(
//                            systemPrompt, userPrompt.toString(), studentImageUrls
//                    );
//
//            // 4. 更新作业状态与结果
//            stuHomework.setCompleteAndCorrect(3); // 标记为已批改
//            stuHomework.setScore(correctionResult.getScore());
//            stuHomework.setTeacherComment(correctionResult.getComment());
//            stuHomework.setCorrectTime(now);
//            stuHomeworkMapper.updateById(stuHomework);
//
//            // 5. 构建返回结果
//            CorrectionResultWithImageUrl resultVO = new CorrectionResultWithImageUrl();
//            resultVO.setScore((double) correctionResult.getScore());
//            resultVO.setTeacherComment(correctionResult.getComment());
//            resultVO.setCorrectedImageUrl(correctionResult.getCorrectedImageUrl());
//
//            return Result.ok(resultVO);
//
//        } catch (ApiException e) {
//            log.error("AI接口调用失败 - 作业ID: {}, 学生ID: {}", homeworkId, studentId, e);
//            return Result.error("AI批改失败: " + e.getMessage());
//        } catch (Exception e) {
//            log.error("作业批改流程异常 - 作业ID: {}, 学生ID: {}", homeworkId, studentId, e);
//            return Result.error("系统异常: " + e.getMessage());
//        }
//    }
//
//    /**
//     * 从URL下载图片数据
//     */
//    private byte[] downloadImageFromUrl(String imageUrl) throws Exception {
//        java.net.URL url = new java.net.URL(imageUrl);
//        try (java.io.InputStream inputStream = url.openStream()) {
//            return org.apache.commons.io.IOUtils.toByteArray(inputStream);
//        }
//    }
//
//    /**
//     * 根据URL获取图片格式
//     */
//    private String getImageFormatFromUrl(String imageUrl) {
//        if (imageUrl.toLowerCase().endsWith(".jpg") || imageUrl.toLowerCase().endsWith(".jpeg")) {
//            return "jpeg";
//        } else if (imageUrl.toLowerCase().endsWith(".png")) {
//            return "png";
//        } else if (imageUrl.toLowerCase().endsWith(".gif")) {
//            return "gif";
//        } else {
//            // 尝试从Content-Type获取格式
//            try {
//                java.net.URL url = new java.net.URL(imageUrl);
//                String contentType = url.openConnection().getContentType();
//                if (contentType != null) {
//                    if (contentType.contains("jpeg") || contentType.contains("jpg")) {
//                        return "jpeg";
//                    } else if (contentType.contains("png")) {
//                        return "png";
//                    } else if (contentType.contains("gif")) {
//                        return "gif";
//                    }
//                }
//            } catch (Exception e) {
//                log.warn("获取图片格式失败: {}", imageUrl, e);
//            }
//            return "jpeg"; // 默认格式
//        }
//    }
//
//
//    // 在类内部添加这个类定义，放在其他类定义之后
//    public static class CorrectionResultWithImageUrl {
//        private Double score;
//        private String teacherComment;
//        private String correctedImageUrl;
//
//        // Getters and setters
//        public Double getScore() { return score; }
//        public void setScore(Double score) { this.score = score; }
//
//        public String getTeacherComment() { return teacherComment; }
//        public void setTeacherComment(String teacherComment) { this.teacherComment = teacherComment; }
//
//        public String getCorrectedImageUrl() { return correctedImageUrl; }
//        public void setCorrectedImageUrl(String correctedImageUrl) { this.correctedImageUrl = correctedImageUrl; }
//    }


}




