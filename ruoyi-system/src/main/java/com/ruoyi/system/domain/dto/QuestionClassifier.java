package com.ruoyi.system.domain.dto;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

// QuestionClassifier.java
public class QuestionClassifier {
    private static final List<String> MAJOR_KEYWORDS = Arrays.asList( // 改用Arrays.asList
            "专业推荐", "分流建议", "选哪个专业", "专业选择",
            "专业适配", "方向推荐", "志愿填报","专业分流"
    );

    public static boolean isMajorRecommendationQuestion(String question) {
        String cleaned = question.replaceAll("[？?]", "").toLowerCase();
        for (String keyword : MAJOR_KEYWORDS) { // 用传统循环替代流式API
            if (cleaned.contains(keyword.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
}
