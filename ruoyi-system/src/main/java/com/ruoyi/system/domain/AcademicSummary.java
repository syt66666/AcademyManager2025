package com.ruoyi.system.domain;

import lombok.Data;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class AcademicSummary {
    private BigDecimal gpa;
    private Integer totalCredits;
    private String corePerformance;

    public String toPromptString() {
        return String.format(
                "GPA：%.2f\n累计学分：%d\n核心课程表现：%s",
                gpa, totalCredits, corePerformance
        );
    }
}
