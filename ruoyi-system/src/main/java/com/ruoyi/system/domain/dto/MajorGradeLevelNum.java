package com.ruoyi.system.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MajorGradeLevelNum {
    private String majorId; // 专业ID
    private Integer gradeA; // A级人数
    private Integer gradeB; // B级人数
    private Integer gradeC; // C级人数
}

