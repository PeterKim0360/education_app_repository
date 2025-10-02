package com.zjxu.educationapp.modules.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data

    // 批改结果封装类
class CorrectionResult {
        private final int score;
        private final String comment;

        public CorrectionResult(int score, String comment) {
            this.score = score;
            this.comment = comment;
        }
    }