package com.zjxu.educationapp.common.utils;

import com.zjxu.educationapp.common.constant.QuestionShowType;
import com.zjxu.educationapp.modules.entity.Question;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * AI题目解析器：负责将AI生成的文本格式题目（无$和\特殊符号）解析为Question实体列表
 * 核心能力：适配纯文本数学表达式、移除填空题末尾冒号、正确拆分选项、兼容全题型、异常日志追踪
 */
@Slf4j
@Component
public class AiQuestionParser {
    // ============================= 正则表达式定义（适配无特殊符号格式）=============================
    /** 匹配题型标记（如【单选题】【多选题】【判断题】【填空题】） */
    private static final Pattern BASE_TYPE_MARK_PATTERN = Pattern.compile("【(单选题|多选题|判断题|填空题)】");
    /** 匹配单个选项（支持"A. XXX" "A、XXX"格式，兼容纯文本数学表达式） */
    private static final Pattern SINGLE_OPTION_PATTERN = Pattern.compile("[A-D][、.]\\s+[^\\n]+");
    /** 匹配单选题答案（如"答案：C" "答案:C"） */
    private static final Pattern SINGLE_ANSWER_PATTERN = Pattern.compile("答案[：:]\\s*([A-D])\\s*");
    /** 匹配多选题答案（如"答案：A,B,C" "答案:A、B、C"） */
    private static final Pattern MULTIPLE_ANSWER_PATTERN = Pattern.compile("答案[：:]\\s*([A-D,、]+)\\s*");
    /** 匹配判断题答案（如"答案：对" "答案:错"） */
    private static final Pattern JUDGMENT_ANSWER_PATTERN = Pattern.compile("答案[：:]\\s*(对|错)\\s*");
    /** 匹配填空题答案（如"答案：5" "答案：[1, +infty)"，兼容纯文本答案） */
    private static final Pattern FILL_ANSWER_PATTERN = Pattern.compile("答案[：:]\\s*([^\\n]+)\\s*");
    /** 清理多余符号（移除可能残留的$和\，确保输出纯净） */
    private static final Pattern EXTRA_SYMBOL_PATTERN = Pattern.compile("\\$|\\\\");
    /** 匹配填空题题干末尾的多余冒号（包括“：”“:”，兼容带句号/空格的场景，如“______。：”“______ ：”） */
    private static final Pattern FILL_END_COLON_PATTERN = Pattern.compile("([。\\s])[：:]$");


    /**
     * 核心方法：解析AI生成的无特殊符号文本，转换为Question列表
     * @param aiText AI生成的原始题目文本（需包含题型标记+题干+选项+答案，无$和\）
     * @return 解析后的Question列表（空列表表示无有效题目）
     */
    public List<Question> parseAiText(String aiText) {
        // 入参校验：空文本直接返回空列表
        if (aiText == null || aiText.trim().isEmpty()) {
            log.warn("AI生成的题目文本为空，无法解析");
            return new ArrayList<>();
        }
        // 预处理：移除所有可能残留的$和\符号，避免解析干扰
        String cleanedAiText = EXTRA_SYMBOL_PATTERN.matcher(aiText.trim()).replaceAll("");

        // 步骤1：提取所有题型标记和对应的题目内容（按标记分割）
        Matcher markMatcher = BASE_TYPE_MARK_PATTERN.matcher(cleanedAiText);
        List<String> typeMarkList = new ArrayList<>(); // 存储题型标记（如【单选题】）
        List<String> questionContentList = new ArrayList<>(); // 存储题目内容（题干+选项+答案）
        int lastMatchEnd = 0;

        while (markMatcher.find()) {
            String typeMark = markMatcher.group();
            String questionContent = cleanedAiText.substring(lastMatchEnd, markMatcher.start()).trim();

            // 若标记前有非空内容，加入内容列表（避免漏题）
            if (!questionContent.isEmpty()) {
                questionContentList.add(questionContent);
            }
            typeMarkList.add(typeMark);
            lastMatchEnd = markMatcher.end();
        }

        // 处理最后一道题（标记后的剩余内容）
        String lastQuestionContent = cleanedAiText.substring(lastMatchEnd).trim();
        if (!lastQuestionContent.isEmpty()) {
            questionContentList.add(lastQuestionContent);
        }

        // 步骤2：校验标记与内容数量一致性（防止数组越界）
        if (typeMarkList.size() != questionContentList.size()) {
            log.warn("题型标记与题目内容数量不匹配，标记数：{}，内容数：{}，可能存在格式异常",
                    typeMarkList.size(), questionContentList.size());
        }

        // 步骤3：逐题解析（取最小长度，确保安全）
        List<Question> questionList = new ArrayList<>();
        int validCount = Math.min(typeMarkList.size(), questionContentList.size());
        for (int i = 0; i < validCount; i++) {
            String typeMark = typeMarkList.get(i);
            String questionContent = questionContentList.get(i);
            String typeDesc = typeMark.replaceAll("[【】]", ""); // 提取题型（如"单选题"）

            // 转换题型枚举（无法识别则跳过）
            QuestionShowType questionType = getQuestionTypeByDesc(typeDesc);
            if (questionType == null) {
                log.warn("无法识别的题型标记：{}，跳过题目，内容：{}", typeMark, questionContent);
                continue;
            }

            // 初始化Question对象
            Question question = new Question();
            question.setId(i + 1); // 按解析顺序生成ID
            question.setShowType(questionType.getCode()); // 题型编码（1=单选，2=多选等）
            question.setBlankAnswers(null); // 非填空题默认空

            try {
                // 按题型调用解析逻辑
                switch (questionType) {
                    case SINGLE_CHOICE:
                        parseSingleChoice(questionContent, question, typeMark);
                        break;
                    case MULTIPLE_CHOICE:
                        parseMultipleChoice(questionContent, question, typeMark);
                        break;
                    case JUDGMENT:
                        parseJudgment(questionContent, question, typeMark);
                        break;
                    case FILL_BLANK:
                        parseFillBlank(questionContent, question, typeMark); // 填空题单独处理末尾冒号
                        break;
                    default:
                        log.warn("未实现的题型解析逻辑：{}，跳过题目", typeDesc);
                        continue;
                }

                // 校验解析结果有效性（防止空题干/空答案）
                if (isValidQuestion(question, questionType)) {
                    questionList.add(question);
                } else {
                    log.warn("解析后的题目无效（题干/答案缺失），题型：{}，内容：{}", typeDesc, questionContent);
                }
            } catch (Exception e) {
                log.error("解析题目时发生异常，题型：{}，内容：{}", typeDesc, questionContent, e);
            }
        }

        log.info("AI题目解析完成，原始文本长度：{}，有效题目数量：{}", cleanedAiText.length(), questionList.size());
        return questionList;
    }


    /**
     * 解析单选题（适配纯文本数学表达式，如x²、√(x-1)）
     * @param content 单题完整内容（题干+选项+答案）
     * @param question 待填充的Question对象
     * @param typeMark 题型标记（如【单选题】）
     */
    private void parseSingleChoice(String content, Question question, String typeMark) {
        // 1. 提取答案（优先提取，避免干扰选项解析）
        Matcher answerMatcher = SINGLE_ANSWER_PATTERN.matcher(content);
        String correctAnswer = null;
        if (answerMatcher.find()) {
            correctAnswer = answerMatcher.group(1).trim();
            content = content.substring(0, answerMatcher.start()).trim(); // 移除答案部分
        }

        // 2. 提取并拆分选项（核心优化：兼容纯文本数学格式，去重排序）
        List<String> options = new ArrayList<>();
        Matcher optionMatcher = SINGLE_OPTION_PATTERN.matcher(content);
        while (optionMatcher.find()) {
            String option = optionMatcher.group().trim();
            // 清理选项：移除换行符、多余空格，确保格式统一
            option = option.replaceAll("\\n", "").replaceAll("\\s+", " ");
            if (!options.contains(option)) {
                options.add(option);
            }
        }

        // 3. 提取题干（移除选项，拼接题型标记）
        String pureStem = content.replaceAll(SINGLE_OPTION_PATTERN.pattern(), "").trim()
                .replaceAll("\\s+", " ") // 合并多余空格
                .replaceAll("：$", "："); // 单选/多选题保留末尾冒号（如需统一可同步修改）
        String fullStem = typeMark + pureStem; // 回写题型标记

        // 4. 填充Question属性
        question.setContent(fullStem);
        question.setChooses(options);
        question.setAnswers(correctAnswer != null ? Arrays.asList(correctAnswer) : new ArrayList<>());
    }


    /**
     * 解析多选题（适配纯文本数学表达式）
     * @param content 单题完整内容（题干+选项+答案）
     * @param question 待填充的Question对象
     * @param typeMark 题型标记（如【多选题】）
     */
    private void parseMultipleChoice(String content, Question question, String typeMark) {
        // 1. 提取答案（支持"A,B,C" "A、B、C"格式，过滤无效字符）
        Matcher answerMatcher = MULTIPLE_ANSWER_PATTERN.matcher(content);
        List<String> correctAnswers = new ArrayList<>();
        if (answerMatcher.find()) {
            String answerStr = answerMatcher.group(1).trim();
            correctAnswers = Arrays.stream(answerStr.replace("、", ",").split(","))
                    .map(String::trim)
                    .filter(letter -> letter.matches("[A-D]")) // 仅保留A-D选项
                    .distinct() // 去重（防止AI生成重复答案）
                    .collect(Collectors.toList());
            content = content.substring(0, answerMatcher.start()).trim(); // 移除答案部分
        }

        // 2. 提取并拆分选项（逻辑同单选题，兼容纯文本格式）
        List<String> options = new ArrayList<>();
        Matcher optionMatcher = SINGLE_OPTION_PATTERN.matcher(content);
        while (optionMatcher.find()) {
            String option = optionMatcher.group().trim();
            option = option.replaceAll("\\n", "").replaceAll("\\s+", " ");
            if (!options.contains(option)) {
                options.add(option);
            }
        }

        // 3. 提取题干（拼接题型标记）
        String pureStem = content.replaceAll(SINGLE_OPTION_PATTERN.pattern(), "").trim()
                .replaceAll("\\s+", " ")
                .replaceAll("：$", "："); // 单选/多选题保留末尾冒号（如需统一可同步修改）
        String fullStem = typeMark + pureStem;

        // 4. 填充Question属性
        question.setContent(fullStem);
        question.setChooses(options);
        question.setAnswers(correctAnswers);
    }


    /**
     * 解析判断题（固定选项：A.对，B.错，适配纯文本题干）
     * @param content 单题完整内容（题干+答案）
     * @param question 待填充的Question对象
     * @param typeMark 题型标记（如【判断题】）
     */
    private void parseJudgment(String content, Question question, String typeMark) {
        // 1. 提取答案（支持"对/错"或"A/B"格式，统一为选项字母）
        Matcher answerMatcher = JUDGMENT_ANSWER_PATTERN.matcher(content);
        String correctAnswer = null;
        if (answerMatcher.find()) {
            String rawAnswer = answerMatcher.group(1).trim();
            correctAnswer = "对".equals(rawAnswer) || "A".equals(rawAnswer) ? "A" : "B";
            content = content.substring(0, answerMatcher.start()).trim(); // 移除答案部分
        }

        // 2. 固定判断题选项（无需从AI文本提取，确保格式统一）
        List<String> options = Arrays.asList("A. 对", "B. 错");

        // 3. 提取题干（移除残留选项，拼接题型标记）
        String pureStem = content.replaceAll("\\s*[AB][、.]\\s*[对错]", "").trim()
                .replaceAll("\\s+", " ")
                .replaceAll("：$", "："); // 判断题保留末尾冒号（如需统一可同步修改）
        String fullStem = typeMark + pureStem;

        // 4. 填充Question属性
        question.setContent(fullStem);
        question.setChooses(options);
        question.setAnswers(correctAnswer != null ? Arrays.asList(correctAnswer) : new ArrayList<>());
    }


    /**
     * 解析填空题（核心优化：移除题干末尾多余冒号，适配纯文本答案）
     * @param content 单题完整内容（题干+答案）
     * @param question 待填充的Question对象
     * @param typeMark 题型标记（如【填空题】）
     */
    private void parseFillBlank(String content, Question question, String typeMark) {
        // 1. 提取答案（支持纯文本数字、表达式，过滤选项字母）
        Matcher answerMatcher = FILL_ANSWER_PATTERN.matcher(content);
        List<String> correctAnswers = new ArrayList<>();
        if (answerMatcher.find()) {
            String answer = answerMatcher.group(1).trim();
            // 过滤无效答案（排除A-D选项字母，防止误识别）
            if (!answer.matches("[A-D]")) {
                correctAnswers.add(answer);
            }
            content = content.substring(0, answerMatcher.start()).trim(); // 移除答案部分
        }

        // 2. 提取题干（优先匹配带下划线占位符，无占位符则取非空行，核心：移除末尾多余冒号）
        String pureStem = "";
        String[] lines = content.split("\\n|\\r"); // 按换行分割，避免跨行长题干干扰
        for (String line : lines) {
            String trimmedLine = line.trim().replaceAll("\\s+", " ");
            // 匹配填空题占位符（___或____）
            if (trimmedLine.contains("___") || trimmedLine.contains("____")) {
                pureStem = trimmedLine;
                break;
            }
            // 无占位符时取首个非空行作为题干
            if (pureStem.isEmpty() && !trimmedLine.isEmpty()) {
                pureStem = trimmedLine;
            }
        }

        // 核心优化：移除题干末尾的多余冒号（如“______。：”→“______。”，“______ ：”→“______ ”）
        Matcher colonMatcher = FILL_END_COLON_PATTERN.matcher(pureStem);
        if (colonMatcher.find()) {
            pureStem = colonMatcher.replaceAll("$1"); // 保留冒号前的句号/空格，仅删除冒号
        }
        // 额外处理：若题干无句号/空格，仅末尾有冒号（如“______：”），直接删除冒号
        pureStem = pureStem.replaceAll("[：:]$", "");

        // 3. 拼接题型标记，生成最终题干
        String fullStem = typeMark + pureStem;

        // 4. 填充Question属性（填空题无选项，answers设为null）
        question.setContent(fullStem);
        question.setChooses(null);
        question.setAnswers(null);
        question.setBlankAnswers(correctAnswers);
    }


    /**
     * 根据题型描述获取QuestionShowType枚举（如"单选题"→SINGLE_CHOICE）
     * @param typeDesc 题型描述（如"单选题"）
     * @return 对应的枚举，null表示无法识别
     */
    private QuestionShowType getQuestionTypeByDesc(String typeDesc) {
        for (QuestionShowType type : QuestionShowType.values()) {
            if (type.getDesc().equals(typeDesc)) {
                return type;
            }
        }
        return null;
    }


/**
 * 校验解析后的题目是否有效（防止空题干、空答案）
 * @param question 解析后的Question对象
 * @param questionType 题型枚举
 * @return true= valid，false = 无效
 */
private boolean isValidQuestion (Question question, QuestionShowType questionType) {
// 1. 题干不能为空
    if (question.getContent () == null || question.getContent ().trim ().isEmpty ()) {
        log.debug ("无效题目：题干为空，题型：{}", questionType.getDesc ());
        return false;
    }

// 2. 按题型校验答案
    switch (questionType) {
        case SINGLE_CHOICE:
        case MULTIPLE_CHOICE:
        case JUDGMENT:
// 选择类题目：选项和答案都不能为空
            boolean hasValidChooses = question.getChooses () != null && !question.getChooses ().isEmpty ();
            boolean hasValidAnswers = question.getAnswers () != null && !question.getAnswers ().isEmpty ();
            if (!hasValidChooses) log.debug ("无效题目：选项为空，题型：{}", questionType.getDesc ());
            if (!hasValidAnswers) log.debug ("无效题目：答案为空，题型：{}", questionType.getDesc ());
            return hasValidChooses && hasValidAnswers;
        case FILL_BLANK:
// 填空题：答案不能为空（题干已处理末尾冒号，无需额外校验格式）
            boolean hasValidBlankAnswers = question.getBlankAnswers () != null && !question.getBlankAnswers ().isEmpty ();
            if (!hasValidBlankAnswers) log.debug ("无效题目：填空题答案为空");
            return hasValidBlankAnswers;
        default:
            return false;
    }
}
}