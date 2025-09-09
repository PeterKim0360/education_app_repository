package com.zjxu.educationapp.common.constant;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public enum QuestionsType {
    SINGLE_CHOICE("单选题"),
    MULTIPLE_CHOICE("多选题"),
    TRUE_FALSE("判断题"),
    FILL_IN_BLANK("填空题"),
    COMPREHENSIVE_QUESTIONS("综合题");

    private String msg;
    QuestionsType(String msg){
        this.msg=msg;
    }

    public static List<String> toList(){
        List<String> list=new ArrayList<>();
        for (QuestionsType value : values()) {
            list.add(value.getMsg());
        }
        return list;
    }
}
