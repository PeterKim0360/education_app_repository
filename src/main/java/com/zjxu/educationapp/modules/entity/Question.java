package com.zjxu.educationapp.modules.entity;

import lombok.Data;

import java.util.List;

@Data
public class Question {
    private Integer id;
    private String content;
    // showType：用枚举的code赋值（1=单选，2=多选，3=判断，4=填空，5=综合）
    private Integer showType;
    private List<String> chooses;// 选项列表（单选/多选/判断有，填空无）
    private List<String> answers; // 正确答案文本（单选1个，多选多个，判断1个）
    private List<String> blankAnswers; // 填空题答案（不变）
}