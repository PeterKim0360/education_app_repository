package com.zjxu.educationapp.modules.vo;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class ErrorQuestionsVO {
    private Integer questionId;
    //题目文本内容
    private String questionText;
    //下面4个单选专用
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    //多选专用所有选项
    private List<String> options;
    //单选或多选或填空的结果
    private String correctOption;
    //单选或多选或填空的用户答案
    private String userAnswer;
    //是否掌握
    private Boolean isMastered;
    //创建该错题时间
    private Date createdTime;
    //判断题的结果
    private String correctResult;
    //判断题的用户答案
    private String TrueFalseUserAnswer;
}
