package com.ruoyi.system.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MajorStatisticDTO {
    private Integer majorId;
    private String majorName;
    private Integer gradeA;
    private Integer gradeB;
    private Integer gradeC;
    private Integer studentNum;
}
