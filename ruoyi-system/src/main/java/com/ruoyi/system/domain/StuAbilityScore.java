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
    private static final long serialVersionUID = 1L;

    /** 学生ID（关联学生表主键） */
    private String studentId;

    /** 所属书院 */
    @Excel(name = "所属书院")
    private String academy;

    /** 学业成绩（0-100分） */
    @Excel(name = "学业成绩", readConverterExp = "0=-100分")
    private BigDecimal academicScore;

    /** 科创竞赛（0-100分） */
    @Excel(name = "科创竞赛", readConverterExp = "0=-100分")
    private BigDecimal competitionScore;

    /** 文体活动（0-100分） */
    @Excel(name = "文体活动", readConverterExp = "0=-100分")
    private BigDecimal activityScore;

    /** 讲座报告（0-100分） */
    @Excel(name = "讲座报告", readConverterExp = "0=-100分")
    private BigDecimal lectureScore;

    /** 导师指导（0-100分） */
    @Excel(name = "导师指导", readConverterExp = "0=-100分")
    private BigDecimal tutorialScore;
}
