package com.ruoyi.system.domain;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class StuActivityRecord extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private String awardDateBegin;

    private String awardDateEnd;

    /**
     * 主键ID
     */
    private Integer activityId;

    /**
     * 学号
     */
    @Excel(name = "学号")
    private String studentId;

    /**
     * 姓名
     */
    @Excel(name = "姓名")
    private String studentName;
    /**
     * 书院
     */
    private String academy;
    /**
     * 活动名称
     */
    @Excel(name = "活动名称")
    private String activityName;

    /**
     * 活动级别
     */
    @Excel(name = "获奖级别")
    private String activityLevel;

    /**
     * 活动奖项
     */
    @Excel(name = "获奖奖项")
    private String awardLevel;

    /**
     * 折合奖学金分数
     */
    private Long scholarshipPoints;

    /**
     * 获奖日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "获奖日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date awardDate;

    /**
     * 证明材料路径
     */
    private String proofMaterial;

    /**
     * 旧文件路径
     */
    private List<String> existingProofMaterial;

    /**
     * 修读学期
     */
    @Excel(name = "修读学期")
    private String semester;

    /**
     * 提交时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date applyTime;

    /**
     * 审核人姓名
     */
    private String nickName;

    /**
     * 审核状态
     */
    @Excel(name = "审核状态")
    private String auditStatus;

    /**
     * 审核时间
     */
//    @Excel(name = "审核时间", width = 30, dateFormat = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date auditTime;

    /**
     * 审核意见
     */
    private String auditRemark;
}