package com.ruoyi.system.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MajorStatistic {
    private String majorName;
    private String academy;
    private Integer studentNum;
    private Integer type;//1为保留当前专业 2域内 3类内 4创新班
}
