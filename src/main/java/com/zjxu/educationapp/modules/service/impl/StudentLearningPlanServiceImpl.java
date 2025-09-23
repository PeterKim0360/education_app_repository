package com.zjxu.educationapp.modules.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zjxu.educationapp.common.utils.Result;
import com.zjxu.educationapp.modules.entity.LearningPlan;
import com.zjxu.educationapp.modules.entity.StuHomework;
import com.zjxu.educationapp.modules.entity.SubjectAnalysis;
import com.zjxu.educationapp.modules.entity.Subjects;
import com.zjxu.educationapp.modules.mapper.StuHomeworkMapper;
import com.zjxu.educationapp.modules.mapper.SubjectsMapper;
import com.zjxu.educationapp.modules.service.StudentLearningPlanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class StudentLearningPlanServiceImpl implements StudentLearningPlanService {
    @Autowired
    private StuHomeworkMapper stuHomeworkMapper;
    @Autowired
    private SubjectsMapper subjectsMapper;
    @Autowired
    private AIGCService aigcService; // 注入AI服务

    /**
     * 获取学习计划
     *
     * @return
     */
    @Override
    public Result<?> getLearningPlan() {
        try {
            log.info("开始获取学习计划，用户ID: {}", StpUtil.getLoginIdAsLong());
            //根据学生ID查看stu_homework数据库中的所有作业信息
            List<StuHomework> homeworkList = stuHomeworkMapper.selectList(new LambdaQueryWrapper<StuHomework>()
                    .eq(StuHomework::getUserId, StpUtil.getLoginIdAsLong()));

            log.info("查询到作业数量: {}", homeworkList != null ? homeworkList.size() : 0);

            // 分析各科成绩并生成学习建议
            LearningPlan learningPlan = analyzeAndGeneratePlan(homeworkList);

            return Result.ok(learningPlan);
        } catch (Exception e) {
            log.error("获取学习计划时发生异常", e);
            return Result.error("获取学习计划失败: " + e.getMessage());
        }
    }

    /**
     * 分析成绩并生成学习计划
     * @param homeworkList 作业列表
     * @return 学习计划
     */
    private LearningPlan analyzeAndGeneratePlan(List<StuHomework> homeworkList) {
        if (homeworkList == null || homeworkList.isEmpty()) {
            log.info("作业列表为空，返回空学习计划");
            return new LearningPlan(Collections.emptyList(), "暂无作业数据，无法生成学习计划");
        }

        // 按科目分组计算平均分
        Map<String, List<StuHomework>> subjectHomeworkMap = new HashMap<>();
        for (StuHomework homework : homeworkList) {
            // 处理可能的空值情况
            if (homework.getSubjectId() != null) {
                Subjects subject = subjectsMapper.selectById(homework.getSubjectId());
                if (subject != null && subject.getSubjectName() != null) {
                    String subjectName = subject.getSubjectName();
                    subjectHomeworkMap.computeIfAbsent(subjectName, k -> new ArrayList<>()).add(homework);
                }
            }
        }

        List<SubjectAnalysis> subjectAnalyses = new ArrayList<>();

        for (Map.Entry<String, List<StuHomework>> entry : subjectHomeworkMap.entrySet()) {
            String subject = entry.getKey();
            List<StuHomework> subjectHomeworks = entry.getValue();

            // 计算平均分
            OptionalDouble averageScoreOpt = subjectHomeworks.stream()
                    .filter(hw -> hw.getScore() != null)
                    .mapToDouble(StuHomework::getScore)
                    .average();

            double averageScore = averageScoreOpt.orElse(0.0);

            // 计算作业完成次数
            int totalAssignments = subjectHomeworks.size();

            // 生成学习建议
            String suggestion = generateStudySuggestion(subject, averageScore);

            // 计算建议学习时间（小时/周）
            int recommendedHours = calculateRecommendedHours(averageScore);

            subjectAnalyses.add(new SubjectAnalysis(subject, averageScore, totalAssignments, suggestion, recommendedHours));
        }

        // 按平均分排序，低分在前
        subjectAnalyses.sort(Comparator.comparing(SubjectAnalysis::getAverageScore));

        String overallSuggestion = generateOverallSuggestion(subjectAnalyses);

        return new LearningPlan(subjectAnalyses, overallSuggestion);
    }

    /**
     * 根据科目和平均分生成学习建议（使用AI生成）
     * @param subject 科目
     * @param averageScore 平均分
     * @return 学习建议
     */
    private String generateStudySuggestion(String subject, double averageScore) {
        // 定义系统角色提示
        String systemPrompt = "你是一位专业的学习规划大师，擅长根据学生的学习成绩提供个性化的学习建议和未来5天的规划。你的建议应该具体、实用，并且易于学生理解和执行。";

        // 构造用户提示
        String userPrompt = String.format("学生在%s科目中的平均成绩为%.1f分（满分100分）。请根据这个成绩提供详细的学习建议，包括：\n" +
                        "1. 对当前学习状况的简要评估\n" +
                        "2. 2-3条具体可操作的学习改进建议\n" +
                        "3. 推荐的学习方法或策略\n" +
                        "4. 每周建议的学习时间安排和学习规划\n" +
                        "5. 推荐相关教学视频或书籍" +
                        "请用中文回复，语言简洁明了，适合中学生理解。不要使用markdown格式，直接返回纯文本。",
                subject, averageScore);

        try {
            // 调用AI服务生成建议
            String aiSuggestion = aigcService.callAI(systemPrompt, userPrompt);
            log.info("成功为{}科目生成AI学习建议，平均分: {}", subject, averageScore);
            return aiSuggestion;
        } catch (ApiException | NoApiKeyException | InputRequiredException e) {
            log.warn("调用AI服务生成{}科目学习建议失败，使用默认建议: {}", subject, e.getMessage());
            // 如果AI服务调用失败，回退到原来的规则生成方式
            return generateStudySuggestionFallback(subject, averageScore);
        } catch (Exception e) {
            log.error("调用AI服务时发生未预期的异常", e);
            return generateStudySuggestionFallback(subject, averageScore);
        }
    }

    /**
     * 回退方案：当AI服务不可用时使用的规则生成方式
     * @param subject 科目
     * @param averageScore 平均分
     * @return 学习建议
     */
    private String generateStudySuggestionFallback(String subject, double averageScore) {
        if (averageScore < 60) {
            return String.format("你在%s科目上表现较弱，建议重点关注基础知识，多做基础练习题，及时向老师请教疑难问题。", subject);
        } else if (averageScore < 75) {
            return String.format("你在%s科目上有提升空间，建议巩固知识点，定期复习，加强练习。", subject);
        } else if (averageScore < 90) {
            return String.format("你在%s科目表现良好，建议保持当前学习状态，查漏补缺，挑战更高难度题目。", subject);
        } else {
            return String.format("你在%s科目表现优秀，建议继续保持，可以尝试拓展学习相关领域的知识。", subject);
        }
    }

    /**
     * 根据平均分计算推荐学习时间
     * @param averageScore 平均分
     * @return 推荐学习时间（小时/周）
     */
    private int calculateRecommendedHours(double averageScore) {
        if (averageScore < 60) {
            return 5; // 需要加强练习
        } else if (averageScore < 75) {
            return 3; // 需要巩固提高
        } else if (averageScore < 90) {
            return 2; // 保持练习
        } else {
            return 1; // 维持即可
        }
    }

    /**
     * 生成总体建议
     * @param subjectAnalyses 各科目分析结果
     * @return 总体建议
     */
    private String generateOverallSuggestion(List<SubjectAnalysis> subjectAnalyses) {
        if (subjectAnalyses.isEmpty()) {
            return "暂无学习建议";
        }

        // 找出最薄弱的科目
        SubjectAnalysis weakestSubject = subjectAnalyses.get(0);

        if (weakestSubject.getAverageScore() < 60) {
            return String.format("建议优先加强%s科目的学习，合理安排时间，重点关注基础知识掌握。", weakestSubject.getSubject());
        } else {
            return "整体学习情况良好，请继续保持，并针对薄弱环节加强练习。";
        }
    }
}
