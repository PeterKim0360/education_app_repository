package com.zjxu.educationapp.modules.service.impl;

import com.zjxu.educationapp.common.constant.QuestionShowType;
import com.zjxu.educationapp.common.utils.AiQuestionParser;
import com.zjxu.educationapp.common.utils.PageInfo;
import com.zjxu.educationapp.common.utils.Result;
import com.zjxu.educationapp.modules.entity.Question;
import com.zjxu.educationapp.modules.vo.QuestionResult;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuestionService {
    private final AIGCService aigcService;
    private final AiQuestionParser aiParser;

    // 保留构造器（依赖注入不可删除）
    public QuestionService(AIGCService aigcService, AiQuestionParser aiParser) {
        this.aigcService = aigcService;
        this.aiParser = aiParser;
    }

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

}