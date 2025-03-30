package com.ruoyi.system.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

// com.ruoyi.project.domain.StudentAbilityScore
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentAbilityScore {
    private String studentId;
    private BigDecimal academicScore; // 标准化后的学业成绩（0-100）
    private BigDecimal competitionScore;
    private BigDecimal activityScore;
    private BigDecimal lectureScore;
    private BigDecimal tutorialScore;
}

