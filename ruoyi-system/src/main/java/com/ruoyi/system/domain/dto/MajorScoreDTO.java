package com.ruoyi.system.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MajorScoreDTO {
    private String studentId;
    private Integer academicScore;
    private Integer majorId;
    private Integer ranking;


}
