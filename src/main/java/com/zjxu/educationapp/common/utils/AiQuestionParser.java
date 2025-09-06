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
@Slf4j
@Component
public class AiQuestionParser {
    // 支持选项用“、”或“.”（如“A、XXX”或“A. XXX”）
    private static final Pattern OPTION_PATTERN = Pattern.compile("\\s*[A-D][、.]\\s+.*");
    // 匹配答案格式（支持“答案：X”或“答案:X”，冒号中英文兼容）
    private static final Pattern ANSWER_PATTERN = Pattern.compile("答案[：:]\\s*([A-D])");
    // 匹配多选题答案格式（支持多个选项，如“A,B,C”或“A、B、C”）
    private static final Pattern MULTIPLE_ANSWER_PATTERN = Pattern.compile("答案[：:]\\s*([A-D,、]+)");
    // 匹配判断题答案格式（对/错）
    private static final Pattern JUDGMENT_ANSWER_PATTERN = Pattern.compile("答案[：:]\\s*(对|错)");
    // 匹配填空题答案格式
    private static final Pattern FILL_ANSWER_PATTERN = Pattern.compile("答案[：:]\\s*(.+)");
    // 匹配基础题型标记（如“【单选题】”）
    private static final Pattern BASE_TYPE_MARK_PATTERN = Pattern.compile("【(单选题|多选题|判断题|填空题)】");

    public List<Question> parseAiText(String aiText) {
        List<Question> questions = new ArrayList<>();
        if (aiText == null || aiText.trim().isEmpty()) {
            return questions;
        }

        // 1. 按题型标记分割每道题（综合型/基础题型通用）
        Matcher markMatcher = BASE_TYPE_MARK_PATTERN.matcher(aiText);
        List<String> baseTypeMarks = new ArrayList<>();
        List<String> questionContents = new ArrayList<>();
        int lastMatchEnd = 0;

        while (markMatcher.find()) {
            baseTypeMarks.add(markMatcher.group());
            String content = aiText.substring(lastMatchEnd, markMatcher.start()).trim();
            if (!content.isEmpty()) {
                questionContents.add(content);
            }
            lastMatchEnd = markMatcher.end();
        }
        String lastContent = aiText.substring(lastMatchEnd).trim();
        if (!lastContent.isEmpty()) {
            questionContents.add(lastContent);
        }

        // 2. 逐题解析
        int validCount = Math.min(baseTypeMarks.size(), questionContents.size());
        for (int i = 0; i < validCount; i++) {
            String mark = baseTypeMarks.get(i);
            String content = questionContents.get(i);
            String baseTypeDesc = mark.replace("【", "").replace("】", "");

            // 通过描述获取枚举
            QuestionShowType baseTypeEnum = getQuestionShowTypeByDesc(baseTypeDesc);
            if (baseTypeEnum == null) {
                continue; // 跳过无法识别的题型
            }

            Integer baseShowType = baseTypeEnum.getCode();

            Question question = new Question();
            question.setId(i + 1);
            question.setShowType(baseShowType);
            question.setBlankAnswers(null);

            // 调用对应的解析方法
            if (baseShowType == 1) {
                parseSingleChoice(content, question);
            } else if (baseShowType == 2) {
                parseMultipleChoice(content, question);
            } else if (baseShowType == 3) {
                parseJudgment(content, question);
            } else if (baseShowType == 4) {
                parseFillBlank(content, question);
            }

            questions.add(question); // 直接添加所有解析出的题目，不做过滤
        }

        return questions;
    }

    /**
     * 解析单选题
     */
    private void parseSingleChoice(String content, Question question) {
        List<String> chooses = new ArrayList<>();
        List<String> answers = new ArrayList<>();
        String questionStem = "";

        // 提取答案行
        Matcher answerMatcher = ANSWER_PATTERN.matcher(content);
        String correctAnswerLetter = null;
        if (answerMatcher.find()) {
            correctAnswerLetter = answerMatcher.group(1);
            content = content.substring(0, answerMatcher.start()).trim();
        }

        // 提取选项
        Pattern optionPattern = Pattern.compile("[A-D][、.]\\s*[^A-D]+");
        Matcher optionMatcher = optionPattern.matcher(content);
        while (optionMatcher.find()) {
            chooses.add(optionMatcher.group().trim());
        }

        // 仅添加选项字母到answers
        if (correctAnswerLetter != null && !correctAnswerLetter.isEmpty()) {
            answers.add(correctAnswerLetter);
        }

        // 提取题干
        questionStem = content.replaceAll("[A-D][、.]\\s*[^A-D]+", "")
                .trim().replaceAll("\\s+", " ").replaceAll("：$", "：");

        // 填充属性
        question.setContent(questionStem);
        question.setChooses(chooses);
        question.setAnswers(answers);
        question.setBlankAnswers(null);
    }

    /**
     * 解析多选题
     */
    private void parseMultipleChoice(String content, Question question) {
        List<String> chooses = new ArrayList<>();
        List<String> answers = new ArrayList<>();
        String questionStem = "";

        // 提取答案
        Matcher answerMatcher = MULTIPLE_ANSWER_PATTERN.matcher(content);
        List<String> correctLetters = new ArrayList<>();
        if (answerMatcher.find()) {
            String answerStr = answerMatcher.group(1);
            correctLetters = Arrays.stream(answerStr.replace("、", ",").split(","))
                    .map(String::trim)
                    .filter(letter -> letter.matches("[A-D]"))
                    .collect(Collectors.toList());
            content = content.substring(0, answerMatcher.start()).trim();
        }

        // 提取选项
        Pattern optionPattern = Pattern.compile("[A-D][、.]\\s*[^A-D]+");
        Matcher optionMatcher = optionPattern.matcher(content);
        while (optionMatcher.find()) {
            chooses.add(optionMatcher.group().trim());
        }

        // 添加选项字母到answers
        answers.addAll(correctLetters);

        // 提取题干
        questionStem = content.replaceAll("[A-D][、.]\\s*[^A-D]+", "")
                .trim().replaceAll("\\s+", " ").replaceAll("：$", "：");

        // 填充属性
        question.setContent(questionStem);
        question.setChooses(chooses);
        question.setAnswers(answers);
        question.setBlankAnswers(null);
    }

    /**
     * 解析判断题
     */
    private void parseJudgment(String content, Question question) {
        // 固定判断题选项（A=对，B=错）
        List<String> chooses = Arrays.asList("A、对", "B、错");
        List<String> answers = new ArrayList<>();
        String questionStem = "";

        // 1. 关键：从题干中强制提取答案（兼容“答案：B”“答案：B：”等异常格式）
        // 正则匹配“答案：X”（X为A/B/对/错，允许末尾有多余冒号）
        Matcher answerMatcher = Pattern.compile("答案[：:]\\s*([AB对错])").matcher(content);
        String correctAnswer = null;
        if (answerMatcher.find()) {
            correctAnswer = answerMatcher.group(1).trim(); // 提取答案（如“B”或“错”）
            // 移除题干中的答案部分（从答案标识开始到文本末尾，包括多余冒号）
            content = content.substring(0, answerMatcher.start()).trim();
        }

        // 2. 清理题干：移除可能残留的选项标识（如“A、正确”“B、错误”），并补全冒号
        questionStem = content.replaceAll("\\s*[AB][、.]\\s*[对错正确错误]+", "") // 移除选项残留
                .trim()
                .replaceAll("\\s+", " ") // 清除多余空格
                .replaceAll("：?$", "："); // 确保题干末尾仅1个冒号

        // 3. 映射答案为选项字母（A=对，B=错），填充answers
        if (correctAnswer != null) {
            switch (correctAnswer) {
                case "对":
                case "A":
                    answers.add("A");
                    break;
                case "错":
                case "B":
                    answers.add("B");
                    break;
                default:
                    // 异常答案格式，记录日志（便于后续优化AI提示词）
                    log.warn("判断题答案格式异常，题干：{}，提取的答案：{}", questionStem, correctAnswer);
                    break;
            }
        } else {
            // 未提取到答案，记录日志（提示AI生成格式不规范）
            log.warn("判断题未提取到答案，原始题干：{}", content);
        }

        // 4. 填充题目属性
        question.setContent(questionStem);
        question.setChooses(chooses);
        question.setAnswers(answers); // 确保answers不为空（正确时存“A”或“B”）
        question.setBlankAnswers(null);
    }

    /**
     * 解析填空题
     */
    private void parseFillBlank(String content, Question question) {
        String questionStem = "";
        List<String> blankAnswers = new ArrayList<>();

        // 提取答案
        Matcher answerMatcher = FILL_ANSWER_PATTERN.matcher(content);
        String answerText = "";
        if (answerMatcher.find()) {
            answerText = answerMatcher.group(1).trim();
            if (answerText.matches("[A-D]")) {
                answerText = "";
            }
        }
        if (!answerText.isEmpty()) {
            blankAnswers.add(answerText);
        }

        // 提取题干
        String[] lines = content.split("\\n|\\r");
        for (String line : lines) {
            line = line.trim().replaceAll("\\s+", " ");
            if (line.startsWith("答案：") || line.startsWith("答案:")) {
                continue;
            }
            if (line.contains("___") || line.contains("______")) {
                questionStem = line + "：";
                break;
            }
            if (questionStem.isEmpty() && !line.isEmpty()) {
                questionStem = line + "：";
            }
        }

        // 填充属性
        question.setContent(questionStem);
        question.setChooses(null);
        question.setAnswers(null);
        question.setBlankAnswers(blankAnswers);
    }

    /**
     * 通过描述获取QuestionShowType枚举
     */
    private QuestionShowType getQuestionShowTypeByDesc(String desc) {
        for (QuestionShowType type : QuestionShowType.values()) {
            if (type.getDesc().equals(desc)) {
                return type;
            }
        }
        return null;
    }
}
