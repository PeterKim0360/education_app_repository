package com.zjxu.educationapp.common.constant;

import lombok.Getter;
import java.util.List;

/**
 * 题目展示类型枚举（包含综合型）
 */
@Getter
public enum QuestionShowType {
    // 基础题型
    SINGLE_CHOICE(1, "单选题", "基础题型，只有1个正确答案"),
    MULTIPLE_CHOICE(2, "多选题", "基础题型，有2个及以上正确答案"),
    JUDGMENT(3, "判断题", "基础题型，答案为对/错"),
    FILL_BLANK(4, "填空题", "基础题型，需填写文本答案"),
    // 新增：综合型（包含多种基础题型，默认比例：单选30%、多选25%、判断25%、填空20%）
    COMPREHENSIVE(5, "综合题", "混合题型，包含单选、多选、判断、填空",
            List.of(
                    new BaseTypeRatio(1, 0.3),  // 单选占30%
                    new BaseTypeRatio(2, 0.25), // 多选占25%
                    new BaseTypeRatio(3, 0.25), // 判断占25%
                    new BaseTypeRatio(4, 0.2)   // 填空占20%
            ));

    private final Integer code;          // 题型编码
    private final String desc;           // 题型描述
    private final String remark;         // 补充说明
    private final List<BaseTypeRatio> baseTypeRatios; // 综合型包含的基础题型及比例（非综合型为null）

    // 基础题型构造器（无比例）
    QuestionShowType(Integer code, String desc, String remark) {
        this.code = code;
        this.desc = desc;
        this.remark = remark;
        this.baseTypeRatios = null;
    }

    // 综合型构造器（含基础题型比例）
    QuestionShowType(Integer code, String desc, String remark, List<BaseTypeRatio> baseTypeRatios) {
        this.code = code;
        this.desc = desc;
        this.remark = remark;
        this.baseTypeRatios = baseTypeRatios;
    }

    /**
     * 工具方法：根据题型描述获取枚举（支持“综合型”）
     */
    public static QuestionShowType getByDesc(String desc) {
        for (QuestionShowType type : QuestionShowType.values()) {
            if (type.getDesc().equals(desc)) {
                return type;
            }
        }
        throw new IllegalArgumentException("无效题型：" + desc);
    }

    /**
     * 内部类：综合型包含的基础题型及比例
     */
    @Getter
    public static class BaseTypeRatio {
        private final Integer baseTypeCode; // 基础题型编码（如1=单选）
        private final double ratio;         // 占比（0~1）

        public BaseTypeRatio(Integer baseTypeCode, double ratio) {
            this.baseTypeCode = baseTypeCode;
            this.ratio = ratio;
        }

        /**
         * 根据总数量计算当前基础题型的生成数量（四舍五入）
         */
        public int calculateCount(int totalCount) {
            return (int) Math.round(totalCount * ratio);
        }
    }
}