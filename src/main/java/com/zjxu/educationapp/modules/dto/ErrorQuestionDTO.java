package com.zjxu.educationapp.modules.dto;

import lombok.Data;

import java.util.List;

@Data
public class ErrorQuestionDTO {
    //TODO
    //该题目对应课程
    private Integer subjectId;
    //题干内容
    private String QuestionText;
    //题型类型code=1->单选，code=2->多选，code=3->判断，code=4->填空，code=5->综合
    private Integer code;
    //如果是单选，不是则"",其中C和D可以是空但A和B必须有值,这是题目内容
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    //单选用户的答案
    //单选正确的答案

    //如果是多选,不是则为""
    private List<String> options;
    //如果是判断


}
