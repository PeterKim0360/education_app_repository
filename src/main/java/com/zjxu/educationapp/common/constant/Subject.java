package com.zjxu.educationapp.common.constant;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
@Slf4j
@Getter
public enum Subject {
    HIGHER_MATHEMATICS("数学类",1,"高等数学"),
    LINEAR_ALGEBRA("数学类",2,"线性代数"),
    PROBABILITY_THEORY_AND_MATHEMATICAL_STATISTICS("数学类",3,"概率论与数理统计"),
    FUNDAMENTALS_OF_MARXISM("思想政治类",4,"马克思主义基本原理"),
    SITUATION_AND_POLICY("思想政治类",5,"形势与政策"),
    IDEOLOGICAL_AND_MORAL_CULTIVATION_AND_LEGAL_FOUNDATION("思想政治类",6,"思想道德修养与法律基础"),
    INTRODUCTION_TO_MAO_ZEDONG_THOUGHT_AND_THE_THEORETICAL_SYSTEM_OF_SOCIALISM_WITH_CHINESE_CHARACTERISTICS("思想政治类",7,"毛泽东思想和中国特色社会主义理论体系概论"),
    DATA_STRUCTURE("计算机类",8,"数据结构"),
    PRINCIPLES_OF_COMPUTER_COMPOSITION("计算机类",9,"计算机组成原理"),
    OPERATING_SYSTEM("计算机类",10,"操作系统"),
    NETWORK("计算机类",11,"计算机网络"),
    DATABASE_PRINCIPLES("计算机类",12,"数据库原理");

   private String type;
   private int id;
   private String msg;

    Subject(String type,int id,String msg){
        this.type=type;
        this.id=id;
        this.msg=msg;
    }

    public static List<Map<String , Map<Integer,String>>> toList(){
        List<Map<String,Map<Integer,String>>> list=new ArrayList<>();
        Map<String,Map<Integer,String>> mapMap=new LinkedHashMap<>();
        for (Subject subject : values()) {
            if (!mapMap.containsKey(subject.getType())){
                mapMap.put(subject.getType(),new LinkedHashMap<>());
            }
            mapMap.get(subject.getType()).put(subject.getId(),subject.getMsg());
        }
        for (Map.Entry<String, Map<Integer, String>> entry : mapMap.entrySet()) {
            Map<String, Map<Integer,String>> map = new LinkedHashMap<>();
            map.put(entry.getKey(),entry.getValue());
            log.info("map:{}",map);
            list.add(map);
        }
        return list;
    }

}
