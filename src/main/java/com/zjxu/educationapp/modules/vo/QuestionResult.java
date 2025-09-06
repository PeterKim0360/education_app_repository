package com.zjxu.educationapp.modules.vo;

import com.zjxu.educationapp.common.utils.PageInfo;
import com.zjxu.educationapp.modules.entity.Question;
import lombok.Data;
import java.util.List;

// 题目返回结果总实体类（最终JSON格式）
@Data
public class QuestionResult {
    private Integer code;    // 状态码（0=成功）
    private String message;  // 提示信息（成功时为空字符串）
    private DataDTO data;    // 数据体（嵌套questions和page）

    // 内部类：对应JSON中的data对象
    @Data
    public static class DataDTO {
        private List<Question> questions; // 题目列表数组
        private PageInfo page;            // 分页信息
    }
}