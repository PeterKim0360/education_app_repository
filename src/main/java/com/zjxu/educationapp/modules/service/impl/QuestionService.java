package com.zjxu.educationapp.modules.service.impl;

import com.zjxu.educationapp.common.constant.QuestionShowType;
import com.zjxu.educationapp.common.utils.AiQuestionParser;
import com.zjxu.educationapp.common.utils.PageInfo;
import com.zjxu.educationapp.modules.entity.Question;
import com.zjxu.educationapp.modules.vo.QuestionResult;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuestionService {
    private final AIGCService aigcService;
    private final AiQuestionParser aiParser;

    // 题目数量限制（避免生成过多导致性能问题）
    private static final int MIN_QUESTION_COUNT = 1;
    private static final int MAX_QUESTION_COUNT = 50;

    public QuestionService(AIGCService aigcService, AiQuestionParser aiParser) {
        this.aigcService = aigcService;
        this.aiParser = aiParser;
    }

    /**
     * 生成题目（支持动态数量和分页）
     * @param questionType 题目主题（如"高等数学"）
     * @param questionStyle 题型（如"单选题"）
     * @param totalCount 用户指定的总题目数量（动态输入）
     * @param pageNum 当前页码
     * @param pageSize 每页显示数量
     */
    public QuestionResult generateQuestions(
            String questionType,
            String questionStyle,
            @RequestParam(defaultValue = "10") int totalCount, // 用户动态输入的总题数
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "5") int pageSize) throws Exception {

        // 1. 校验题目数量合法性
        if (totalCount < MIN_QUESTION_COUNT || totalCount > MAX_QUESTION_COUNT) {
            throw new IllegalArgumentException(
                    String.format("题目数量必须在%d-%d之间", MIN_QUESTION_COUNT, MAX_QUESTION_COUNT)
            );
        }

        // 2. 校验分页参数合法性
        if (pageNum < 1) pageNum = 1;
        if (pageSize < 1 || pageSize > 20) pageSize = 5; // 限制每页最大数量

        // 3. 获取题型枚举
        QuestionShowType showTypeEnum = getQuestionTypeByDesc(questionStyle);
        if (showTypeEnum == null) {
            throw new IllegalArgumentException("不支持的题型：" + questionStyle);
        }

        // 4. 生成提示词（使用用户指定的totalCount）
        String systemPrompt = buildSystemPrompt(showTypeEnum, questionType, totalCount);
        String userPrompt = buildUserPrompt(showTypeEnum, questionType, totalCount);

        // 5. 调用AI生成指定数量的题目
        String aiText = aigcService.callAI(systemPrompt, userPrompt);
        System.out.println("AI生成的原始文本：\n" + aiText);

        // 6. 解析所有题目
        List<Question> allQuestions = aiParser.parseAiText(aiText);

        // 7. 数量校验
        if (allQuestions.size() > totalCount) {
            allQuestions = allQuestions.subList(0, totalCount); // 超量：取前totalCount道
            System.out.println("AI超量生成，已截断为" + totalCount + "道题");
        } else if (allQuestions.size() < totalCount) {
            // 可选：不足时抛异常（提示AI生成错误），或补空题（不推荐）
            throw new RuntimeException("AI生成题目数量不足，预期" + totalCount + "道，实际" + allQuestions.size() + "道");
        }
        // 7. 执行分页处理
        List<Question> pageQuestions = paginateQuestions(allQuestions, pageNum, pageSize);

        // 8. 构建分页信息
        PageInfo pageInfo = buildPageInfo(pageNum, pageSize, allQuestions.size());

        // 9. 封装返回结果
        QuestionResult result = new QuestionResult();
        result.setCode(200);
        result.setMessage(String.format("成功生成%d道题", allQuestions.size()));

        QuestionResult.DataDTO data = new QuestionResult.DataDTO();
        data.setQuestions(pageQuestions); // 分页后的题目列表
        data.setPage(pageInfo);
        result.setData(data);

        return result;
    }

    /**
     * 构建系统提示词
     */
    private String buildSystemPrompt(QuestionShowType type, String domain, int totalCount) {
        // 通用强制数量约束（所有题型都加）
        String countConstraint = String.format("""
        5. 数量强制校验：
           - 生成完成后，必须在末尾单独一行标注“共%d道题”（如“共10道题”），否则视为无效；
           - 若生成数量≠%d道，直接丢弃所有内容，返回“数量错误”；
           - 禁止重复生成相同题目，禁止多生成或少生成。""", totalCount, totalCount);

        // 综合型题目提示词（拼接数量约束）
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
            你是专业题目生成助手，需生成【%s】领域的综合型题目（共%d道），包含%s，严格遵守以下规则：
            1. 每道题必须以【子题型名称】开头（如"【单选题】"）；
            2. 题干结尾加冒号，选项单独换行，答案格式规范；
            3. 严格按比例生成：%s，总和必须为%d道；
            4. 各题型格式按通用规范执行；
            %s""", // 插入数量强制约束
                    domain, totalCount, ratioDesc, ratioDesc, totalCount, countConstraint);
        }

        // 单一题型提示词（拼接数量约束）
        return String.format("""
        你是专业题目生成助手，需生成【%s】领域的【%s】（共%d道），严格遵守以下规则：
        1. 每道题必须以【%s】开头；
        2. 题干结尾加冒号，选项单独换行（单选/多选）；
        3. 答案格式：
           - 单选/多选：答案：X（或X、Y）
           - 判断：答案：对/错
           - 填空：答案：具体内容
        4. 严格生成%d道题，不可多生成或少生成；
        %s""", // 插入数量强制约束
                domain, type.getDesc(), totalCount, type.getDesc(), totalCount, countConstraint);
    }

    /**
     * 构建用户提示词
     */
    private String buildUserPrompt(QuestionShowType type, String domain, int totalCount) {
        if (type == QuestionShowType.COMPREHENSIVE) {
            return String.format("生成【%s】领域的综合型题目，共%d道，按系统提示格式输出。",
                    domain, totalCount);
        }
        return String.format("生成【%s】领域的【%s】，共%d道，按系统提示格式输出。",
                domain, type.getDesc(), totalCount);
    }


    /**
     * 对题目列表进行分页
     */
    private List<Question> paginateQuestions(List<Question> allQuestions, int pageNum, int pageSize) {
        int startIndex = (pageNum - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, allQuestions.size());

        // 处理超出范围的页码
        if (startIndex >= allQuestions.size()) {
            return new ArrayList<>();
        }

        return allQuestions.subList(startIndex, endIndex);
    }

    /**
     * 构建分页信息
     */
    private PageInfo buildPageInfo(int pageNum, int pageSize, int totalCount) {
        PageInfo pageInfo = new PageInfo();
        pageInfo.setCurrent(pageNum);
        pageInfo.setSize(pageSize);
        pageInfo.setTotal(totalCount);
        pageInfo.setPages(totalCount == 0 ? 0 : (int) Math.ceil((double) totalCount / pageSize));
        return pageInfo;
    }

    /**
     * 通过描述获取题型枚举
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
     * 通过编码获取题型枚举
     */
    private QuestionShowType getQuestionTypeByCode(Integer code) {
        for (QuestionShowType type : QuestionShowType.values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
}
