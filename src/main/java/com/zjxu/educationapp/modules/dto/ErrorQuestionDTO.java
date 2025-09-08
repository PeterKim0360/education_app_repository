package com.zjxu.educationapp.modules.dto;

import lombok.Data;

import java.util.List;

@Data
public class ErrorQuestionDTO {
    //该题目对应课程
    private Integer subjectId;
    //题干内容
    private String questionText;
    //题型类型code=1->单选，code=2->多选，code=3->判断，code=4->填空
    private Integer code;
    //如果是单选，不是则"",其中C和D可以是空但A和B必须有值,这是题目内容
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    //单选用户的答案，不是单选则""
    private String singleUserAnswer;
    //单选正确的答案，不是单选则""
    private String singleCorrectOption;
    //如果是多选,不是则为[]
    private List<String> options;
    //多选用户的答案，不是多选则""
    private String multipleUserAnswer;
    //多选正确的答案，不是多选则""
    private String multipleCorrectOptions;
    //如果是判断，正确答案，不是则默认false
    private Boolean trueFalseCorrectResult=false;
    //用户答案同理
    private Boolean trueFalseUserAnswer=false;
    //如果是填空，正确答案，不是则""
    private String fillInBlankCorrectAnswers;
    //用户答案同理
    private String fillInBlankUserAnswers;
}
