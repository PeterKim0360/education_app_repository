package com.zjxu.educationapp.modules.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zjxu.educationapp.common.constant.ErrorCode;
import com.zjxu.educationapp.common.constant.QuestionShowType;
import com.zjxu.educationapp.common.utils.AiQuestionParser;
import com.zjxu.educationapp.common.utils.PageInfo;
import com.zjxu.educationapp.common.utils.Result;
import com.zjxu.educationapp.modules.controller.QuestionController;
import com.zjxu.educationapp.modules.entity.*;
import com.zjxu.educationapp.modules.mapper.*;
import com.zjxu.educationapp.modules.vo.QuestionResult;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class QuestionService {
    @Autowired
    private SubjectsMapper subjectsMapper;
    @Autowired
    private ErrorQuestionsMapper errorQuestionsMapper;
    @Autowired
    private SingleChoiceMapper singleChoiceMapper;
    @Autowired
    private MultipleChoiceMapper multipleChoiceMapper;
    @Autowired
    private TrueFalseMapper trueFalseMapper;
    @Autowired
    private FillInBlankMapper fillInBlankMapper;
//    private final ChatClient chatClient;
//    private final String SYSTEM_PROMPT="你是专注于「学生错题深度分析、错因拆解、规律总结及学习改进建议」的专业助手，" +
//            "命名为 “错题分析总结提意大师”。核心职责是通过精准提问引导用户提供错题关键信息，" +
//            "基于学科特性、题型逻辑、学习规律，输出针对性强、可落地的错题分析结论与学习优化方案，帮助用户避免同类错误，提升学习效率。";
    private final AIGCService aigcService;
    private final AiQuestionParser aiParser;

    // 保留构造器（依赖注入不可删除）
//    /**
//     * 初始化AIGC服务
//     * @param dashscopeChatModel
//     */
    public QuestionService(AIGCService aigcService, AiQuestionParser aiParser) {
        this.aigcService = aigcService;
        this.aiParser = aiParser;
//        ChatMemory chatMemory = new InMemoryChatMemory();
//        chatClient=ChatClient.builder(dashscopeChatModel)
//                .defaultSystem(SYSTEM_PROMPT)
//                .defaultAdvisors(new MessageChatMemoryAdvisor(chatMemory))
//                .build();
    }
//        public QuestionService(AIGCService aigcService, AiQuestionParser aiParser) {
//        this.aigcService = aigcService;
//        this.aiParser = aiParser;
//    }

    /**
     * 生成题目（移除所有校验，支持动态数量和分页）
     * @param questionType 题目主题（如"高等数学"）
     * @param questionStyle 题型（如"单选题"）
     * @param totalCount 用户指定的总题目数量（无限制）
     * @param pageNum 当前页码（无限制）
     * @param pageSize 每页显示数量（无限制）
     */
    public QuestionResult generateQuestions(
            String questionType,
            String questionStyle,
            int totalCount,
            int pageNum,
            int pageSize) throws Exception {

        // 1. 移除【题目数量合法性校验】：不再限制 totalCount 范围

        // 2. 移除【分页参数合法性校验】：不再修正 pageNum/pageSize

        // 3. 获取题型枚举（保留：若题型不存在，仍需提示，否则后续解析无意义）
        QuestionShowType showTypeEnum = getQuestionTypeByDesc(questionStyle);
        if (showTypeEnum == null) {
            throw new IllegalArgumentException("不支持的题型：" + questionStyle);
        }

        // 4. 生成提示词（简化数量约束：不再强制AI严格生成totalCount道，避免返回“数量错误”）
        String systemPrompt = buildSystemPrompt(showTypeEnum, questionType, totalCount);
        String userPrompt = buildUserPrompt(showTypeEnum, questionType, totalCount);

        // 5. 调用AI生成题目（无校验，直接调用）
        String aiText = aigcService.callAI(systemPrompt, userPrompt);
        System.out.println("AI生成的原始文本：\n" + aiText);

        // 6. 解析所有题目（无校验，直接解析）
        List<Question> allQuestions = aiParser.parseAiText(aiText);

        // 7. 移除【AI生成数量校验】：不再截断超量题目，不再抛数量不足异常
        // （若AI生成数量与totalCount不一致，直接使用实际解析结果）

        // 8. 执行分页处理（保留分页逻辑，超范围时返回空列表）
        List<Question> pageQuestions = paginateQuestions(allQuestions, pageNum, pageSize);

        // 9. 构建分页信息（基于实际解析的题目数量）
        PageInfo pageInfo = buildPageInfo(pageNum, pageSize, allQuestions.size());

        // 10. 封装返回结果（始终返回200成功，提示实际生成数量）
        QuestionResult result = new QuestionResult();
        result.setCode(200);
        result.setMessage(String.format("成功生成%d道题（用户请求%d道）", allQuestions.size(), totalCount));

        QuestionResult.DataDTO data = new QuestionResult.DataDTO();
        data.setQuestions(pageQuestions);
        data.setPage(pageInfo);
        result.setData(data);

        return result;
    }

    /**
     * 构建系统提示词（移除严格数量校验，仅建议生成指定数量）
     */
    private String buildSystemPrompt(QuestionShowType type, String domain, int totalCount) {
        // 简化数量约束：仅“建议”生成totalCount道，不再强制“必须生成”“否则丢弃”
        String looseCountRule = String.format("""
        5. 数量建议：
        - 建议生成%d道题（若无法生成，可返回任意数量，无需返回“数量错误”）；
        - 生成完成后，若方便可在末尾标注“共X道题”，不标注也可；
        """, totalCount);

        // 综合型题目提示词（保留比例描述，移除严格数量校验）
        if (type == QuestionShowType.COMPREHENSIVE) {
            List<QuestionShowType.BaseTypeRatio> ratios = type.getBaseTypeRatios();
            String ratioDesc = ratios.stream()
                    .map(ratio -> {
                        QuestionShowType baseType = getQuestionTypeByCode(ratio.getBaseTypeCode());
                        int count = ratio.calculateCount(totalCount);
                        return baseType.getDesc() + count + "道";
                    })
                    .collect(Collectors.joining("、"));

            return String.format("""
            你是专业题目生成助手，需生成【%s】领域的综合型题目（建议共%d道，包含%s），遵守以下规则：
            1. 每道题必须以【子题型名称】开头（如"【单选题】"）；
            2. 题干结尾加冒号，选项单独换行，答案格式规范；
            3. 建议按比例生成：%s（比例不严格，可灵活调整）；
            4. 各题型格式按通用规范执行；
            %s""", // 插入宽松数量规则
                    domain, totalCount, ratioDesc, ratioDesc, looseCountRule);
        }

        // 单一题型提示词（移除“题型+序号”强制要求，仅保留基础格式，降低AI理解成本）
        return String.format("""
                你是专业题目生成助手，需生成【%s】领域的【%s】（建议共%d道），遵守以下规则：
                1. 每道题必须以【%s】开头（如"【单选题】"）；
                2. 题干结尾加冒号，选项单独换行（单选/多选）；
                3. 答案格式：
                   - 单选/多选：答案：X（或X、Y），单独一行；
                   - 判断：答案：对/错，单独一行；
                   - 填空：答案：具体内容，单独一行；
                %s""", // 插入宽松数量规则
                domain, type.getDesc(), totalCount, type.getDesc(), looseCountRule);
    }

    /**
     * 构建用户提示词（简化描述，仅传递核心需求）
     */
    private String buildUserPrompt(QuestionShowType type, String domain, int totalCount) {
        if (type == QuestionShowType.COMPREHENSIVE) {
            return String.format("生成【%s】领域的综合型题目，建议%d道，按系统提示格式输出。",
                    domain, totalCount);
        }
        return String.format("生成【%s】领域的【%s】，建议%d道，按系统提示格式输出。",
                domain, type.getDesc(), totalCount);
    }

    /**
     * 分页处理（保留：仅处理超范围页码，不校验参数合法性）
     */
    private List<Question> paginateQuestions(List<Question> allQuestions, int pageNum, int pageSize) {
        // 若页码/页大小为负数，直接返回空列表（避免数组越界）
        if (pageNum < 1 || pageSize < 1) {
            return new ArrayList<>();
        }
        int startIndex = (pageNum - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, allQuestions.size());
        // 页码超出范围，返回空列表
        if (startIndex >= allQuestions.size()) {
            return new ArrayList<>();
        }
        return allQuestions.subList(startIndex, endIndex);
    }

    /**
     * 构建分页信息（保留：基于实际数据生成）
     */
    private PageInfo buildPageInfo(int pageNum, int pageSize, int totalCount) {
        PageInfo pageInfo = new PageInfo();
        pageInfo.setCurrent(pageNum < 1 ? 1 : pageNum); // 页码为负时，显示为1（提升前端体验）
        pageInfo.setSize(pageSize < 1 ? 1 : pageSize); // 页大小为负时，显示为1（避免总页数计算异常）
        pageInfo.setTotal(totalCount);
        // 计算总页数（避免除以0）
        int totalPages = (pageSize < 1 || totalCount < 1) ? 0 : (int) Math.ceil((double) totalCount / pageSize);
        pageInfo.setPages(totalPages);
        return pageInfo;
    }

    /**
     * 通过描述获取题型枚举（保留：核心辅助方法）
     */
    private QuestionShowType getQuestionTypeByDesc(String desc) {
        for (QuestionShowType type : QuestionShowType.values()) {
            if (type.getDesc().equals(desc)) {
                return type;
            }
        }
        return null;
    }

    /**
     * 通过编码获取题型枚举（保留：综合题比例计算需要）
     */
    private QuestionShowType getQuestionTypeByCode(Integer code) {
        for (QuestionShowType type : QuestionShowType.values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }

    /**
     * AI 错误题分析
     * @param subjectId 科目ID
     * @return 分析结果
     */
    public Result<QuestionResultSummary> summary(int subjectId) {
        // 1. 验证科目合法性
        Subjects subject = subjectsMapper.selectById(subjectId);
        if (subject == null) {
            log.warn("科目ID:{}不存在，无法生成错题分析", subjectId);
            return Result.error(ErrorCode.SUBJECT_DO_NOT_EXIST);
        }
        String subjectName = subject.getSubjectName();
        long userId = StpUtil.getLoginIdAsLong();
        log.info("用户ID:{}请求生成{}科目错题分析", userId, subjectName);

        // 2. 查询用户该科目的所有错题记录
        List<ErrorQuestions> errorQuestionList = errorQuestionsMapper.selectList(
                new LambdaQueryWrapper<ErrorQuestions>()
                        .eq(ErrorQuestions::getUserId, userId)
                        .eq(ErrorQuestions::getSubjectId, subjectId)
        );

        // 3. 处理无错题场景
        if (errorQuestionList.isEmpty()) {
            log.info("用户ID:{}在{}科目暂无错题", userId, subjectName);
            return Result.ok(new QuestionResultSummary(
                    subjectName,
                    "当前科目暂无错题，建议保持学习节奏，定期巩固知识点",
                    0,
                    Collections.emptyList()
            ));
        }

        // 4. 关联具体题型表，统计各题型错题数 + 获取题目内容
        Map<String, Integer> questionTypeCount = new HashMap<>();
        List<ErrorDetail> errorDetails = new ArrayList<>();

        for (ErrorQuestions eq : errorQuestionList) {
            int questionId = eq.getQuestionId();
            String questionType = "未知题型";
            String questionText = "无题目内容";

            // 单选题
            SingleChoice singleChoice = singleChoiceMapper.selectById(questionId);
            if (singleChoice != null) {
                questionType = "单选题";
                questionText = "选项：A." + singleChoice.getOptionA() +
                        " B." + singleChoice.getOptionB() +
                        (singleChoice.getOptionC() != null ? " C." + singleChoice.getOptionC() : "") +
                        (singleChoice.getOptionD() != null ? " D." + singleChoice.getOptionD() : "");
            }
            // 多选题
            else {
                MultipleChoice multipleChoice = multipleChoiceMapper.selectById(questionId);
                if (multipleChoice != null) {
                    questionType = "多选题";
                    questionText = "选项：" + multipleChoice.getOptions();
                }
                // 判断题
                else {
                    TrueFalse trueFalse = trueFalseMapper.selectById(questionId);
                    if (trueFalse != null) {
                        questionType = "判断题";
                        questionText = "判断内容：" + (trueFalse.getOptions() != null ? trueFalse.getOptions() : "");
                    }
                    // 填空题
                    else {
                        FillInBlank fillInBlank = fillInBlankMapper.selectById(questionId);
                        if (fillInBlank != null) {
                            questionType = "填空题";
                            questionText = "填空内容：" + (fillInBlank.getCorrectAnswers() != null ? fillInBlank.getCorrectAnswers() : "");
                        }
                    }
                }
            }

            // 统计题型数量
            questionTypeCount.put(questionType, questionTypeCount.getOrDefault(questionType, 0) + 1);

            // 封装错题详情
            errorDetails.add(new ErrorDetail(
                    questionType,
                    questionText,
                    eq.getQuestionText(),
                    eq.getIsMastered() ? "已掌握" : "未掌握"
            ));
        }

        // 5. 构造 AI 提示词
        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append("请生成").append(subjectName).append("的错题分析总结报告：\n");
        promptBuilder.append("1. 题型分布：").append(questionTypeCount).append("\n");
        promptBuilder.append("2. 总错题数：").append(errorQuestionList.size()).append("\n");
        promptBuilder.append("3. 错题详情：\n");
        for (ErrorDetail detail : errorDetails) {
            promptBuilder.append("- 题型：").append(detail.getQuestionType())
                    .append("，题目：").append(detail.getQuestionText())
                    .append("，掌握状态：").append(detail.getMasteryStatus()).append("\n");
        }
        promptBuilder.append("要求：分析高频错误题型、潜在知识漏洞，给出至少3条针对性学习建议。");

        // 6. 调用 AI 生成分析结果
        try {
            String aiResponse = aigcService.callAI(
                    "你是专业的教育领域 AI，需生成清晰、易懂的错题分析报告，结构包含标题、题型分布、错误总结、学习建议。",
                    promptBuilder.toString()
            );

            // 7. 解析 AI 返回结果（智能提取建议）
            List<String> suggestions = extractSuggestions(aiResponse);

            QuestionResultSummary resultSummary = new QuestionResultSummary(
                    subjectName,
                    aiResponse, // 保留完整分析报告作为描述
                    errorQuestionList.size(),
                    suggestions
            );


            return Result.ok(resultSummary);
        } catch (Exception e) {
            log.error("调用AI生成错题分析失败，科目：{}", subjectName, e);
            return Result.error("AI分析服务异常，请稍后重试");
        }

    }
    /**
     * 从AI生成的文本中提取“学习建议”部分
     * 支持多种格式：以“学习建议：”、“建议：”、“建议如下：”开头
     */
    private List<String> extractSuggestions(String aiResponse) {
        List<String> suggestions = new ArrayList<>();

        // 匹配“学习建议”或“建议”开头的段落
        String[] lines = aiResponse.split("\\n");
        boolean inSuggestionSection = false;
        StringBuilder currentSuggestion = new StringBuilder();

        for (String line : lines) {
            line = line.trim();

            // 检查是否进入建议区域
            if (line.contains("学习建议") || line.contains("建议：") || line.contains("建议如下")) {
                inSuggestionSection = true;
                continue;
            }

            if (inSuggestionSection) {
                // 跳过空行
                if (line.isEmpty()) {
                    if (currentSuggestion.length() > 0) {
                        suggestions.add(currentSuggestion.toString().trim());
                        currentSuggestion.setLength(0);
                    }
                    continue;
                }

                // 如果是编号项（如 1.、2.），直接添加
                if (line.matches("^\\d+\\. .*")) {
                    if (currentSuggestion.length() > 0) {
                        suggestions.add(currentSuggestion.toString().trim());
                    }
                    currentSuggestion.append(line).append("\n");
                } else {
                    currentSuggestion.append(line).append("\n");
                }
            }
        }

        // 添加最后一项
        if (currentSuggestion.length() > 0) {
            suggestions.add(currentSuggestion.toString().trim());
        }

        return suggestions;
    }




//    /**
//     * AI 错误题分析
//     * @param subjectId
//     * @return
//     */
//    public Result<QuestionResultSummary> summary(int subjectId) {
//        // 1. 验证科目合法性
//        Subjects subject = subjectsMapper.selectById(subjectId);
//        if (subject == null) {
//            log.warn("科目ID:{}不存在，无法生成错题分析", subjectId);
//            return Result.error(ErrorCode.SUBJECT_DO_NOT_EXIST);
//        }
//        String subjectName = subject.getSubjectName();
//        long userId = StpUtil.getLoginIdAsLong();
//        log.info("用户ID:{}请求生成{}科目错题分析", userId, subjectName);
//
//        // 2. 查询用户该科目的所有错题记录（关联具体题型表）
//        List<ErrorQuestions> errorQuestionList = errorQuestionsMapper.selectList(
//                new LambdaQueryWrapper<ErrorQuestions>()
//                        .eq(ErrorQuestions::getUserId, userId)
//                        .eq(ErrorQuestions::getSubjectId, subjectId)
//        );
//
//        // 3. 处理无错题场景
//        if (errorQuestionList.isEmpty()||errorQuestionList==null) {
//            log.info("用户ID:{}在{}科目暂无错题", userId, subjectName);
//            return Result.ok(new QuestionResultSummary(
//                    subjectName,
//                    "当前科目暂无错题，建议保持学习节奏，定期巩固知识点",
//                    0,
//                    Collections.emptyList()
//            ));
//        }
//
//        // 4. 关联具体题型表，统计各题型错题数
//        Map<String, Integer> questionTypeCount = new HashMap<>(); // 题型 -> 错题数
//        List<ErrorDetail> errorDetails = new ArrayList<>(); // 存储每道错题的详细信息（含题型、内容等）
//
//        for (ErrorQuestions eq : errorQuestionList) {
//            int questionId = eq.getQuestionId();
//            // 根据 question_id 关联不同题型表，获取题型和题目内容
//            String questionType = "未知题型";
//            String questionText = "无题目内容";
//
//            // 单选题
//            SingleChoice singleChoice = singleChoiceMapper.selectById(questionId);
//            if (singleChoice != null) {
//                questionType = "单选题";
//                questionText = "选项：A." + singleChoice.getOptionA() + " B." + singleChoice.getOptionB() +
//                        (singleChoice.getOptionC() != null ? " C." + singleChoice.getOptionC() : "") +
//                        (singleChoice.getOptionD() != null ? " D." + singleChoice.getOptionD() : "");
//            }
//            // 多选题
//            else {
//                MultipleChoice multipleChoice = multipleChoiceMapper.selectById(questionId);
//                if (multipleChoice != null) {
//                    questionType = "多选题";
//                    questionText = "选项：" + multipleChoice.getOptions();
//                }
//                // 判断题
//                else {
//                    TrueFalse trueFalse = trueFalseMapper.selectById(questionId);
//                    if (trueFalse != null) {
//                        questionType = "判断题";
//                        questionText = "判断内容：" + (trueFalse.getOptions() != null ? trueFalse.getOptions() : "");
//                    }
//                    // 填空题
//                    else {
//                        FillInBlank fillInBlank = fillInBlankMapper.selectById(questionId);
//                        if (fillInBlank != null) {
//                            questionType = "填空题";
//                            questionText = "填空内容：" + (fillInBlank.getCorrectAnswers() != null ? fillInBlank.getCorrectAnswers() : "");
//                        }
//                    }
//                }
//            }
//
//            // 统计题型数量
//            questionTypeCount.put(questionType, questionTypeCount.getOrDefault(questionType, 0) + 1);
//            // 封装错题详情
//            errorDetails.add(new ErrorDetail(
//                    questionType,
//                    questionText,
//                    eq.getQuestionText(),
//                    eq.getIsMastered() ? "已掌握" : "未掌握"
//            ));
//        }
//
//        // 5. 构造 AI 提示词（包含题型统计和错题详情）
//        StringBuilder promptBuilder = new StringBuilder();
//        promptBuilder.append("请生成").append(subjectName).append("的错题分析总结报告：\n");
//        promptBuilder.append("1. 题型分布：").append(questionTypeCount).append("\n");
//        promptBuilder.append("2. 总错题数：").append(errorQuestionList.size()).append("\n");
//        promptBuilder.append("3. 错题详情：\n");
//        for (ErrorDetail detail : errorDetails) {
//            promptBuilder.append("- 题型：").append(detail.getQuestionType())
//                    .append("，题目：").append(detail.getQuestionText())
//                    .append("，掌握状态：").append(detail.getMasteryStatus()).append("\n");
//        }
//        promptBuilder.append("要求：分析高频错误题型、潜在知识漏洞，给出至少3条针对性学习建议。");
//
//        // 6. 调用 AI 生成分析结果
//        QuestionResultSummary resultSummary = chatClient.prompt()
//                .user(promptBuilder.toString())
//                .system(SYSTEM_PROMPT+"你是专业的教育领域 AI，需生成清晰、易懂的错题分析报告，结构包含标题、题型分布、错误总结、学习建议。")
//                .call()
//                .entity(QuestionResultSummary.class);
//
////        // 补充本地统计的总题数和题型分布（确保数据完整性）
////        resultSummary.setTotalCount(errorQuestionList.size());
////        resultSummary.setQuestionTypeStats(new ArrayList<>(questionTypeCount.entrySet()));
//
//        return Result.ok(resultSummary);
//    }
//
//    // 辅助类：存储错题详情
//    @Data
//    class ErrorDetail {
//        private String questionType;
//        private String questionText;
//        private String originalText;
//        private String masteryStatus;
//
//        public ErrorDetail(String questionType, String questionText, String originalText, String masteryStatus) {
//            this.questionType = questionType;
//            this.questionText = questionText;
//            this.originalText = originalText;
//            this.masteryStatus = masteryStatus;
//        }
//    }
}