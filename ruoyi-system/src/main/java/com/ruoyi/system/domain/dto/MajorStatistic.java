package com.ruoyi.system.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MajorStatistic {
    private Integer subMajorId;
    private String subMajorName;
    private Integer intraClassCount; // 类内任选数量
    private Integer intraDomainCount; // 域内任选数量
}
