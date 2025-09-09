package com.zjxu.educationapp.common.constant;

import lombok.Getter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
@Getter
public enum Province {

    ZHEJIANG_PROVINCE(1,"浙江省"),
    JIANGSU_PROVINCE(2,"江苏省");


    private Integer id;
    private String province;
    Province(Integer id,String province){
        this.id=id;
        this.province=province;
    }

    public static List<Map<Integer,String>> toList(){
        Map<Integer,String> map=new LinkedHashMap<>();
        List<Map<Integer,String>> list=new ArrayList<>();
        for (Province value : values()) {
            map.put(value.getId(), value.getProvince());
        }
        list.add(map);
        return list;
    }
}
