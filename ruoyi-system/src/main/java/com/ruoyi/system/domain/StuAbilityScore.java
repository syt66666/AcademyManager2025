package com.ruoyi.system.domain;

import java.math.BigDecimal;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class StuAbilityScore extends BaseEntity
{
    /** 学生ID（关联学生表主键） */
    private String studentId;
    private String academy;
    private BigDecimal academicScore;
    private BigDecimal competitionScore;
    private BigDecimal activityScore;
    private BigDecimal lectureScore;
    private BigDecimal tutorialScore;
}
