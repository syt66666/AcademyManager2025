package com.ruoyi.system.domain.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class GpaResultDTO {
    private String studentId;
    private BigDecimal rawGpa;       // 原始加权GPA（如3.75）
//    private BigDecimal normalizedGpa; // 标准化后的GPA（0-100）
}