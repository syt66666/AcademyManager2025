package com.ruoyi.system.domain;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

@Data
public class StuCompetitionRecord extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Integer competitionId;

    private String awardDateBegin;

    private String awardDateEnd;

    /** 学号 */
    @Excel(name = "学号")
    private String studentId;

    /** 学生姓名 */
    @Excel(name = "学生姓名")
    private String studentName;

    /** 竞赛名称 */
    @Excel(name = "竞赛名称")
    private String competitionName;

    /** 竞赛级别 */
    @Excel(name = "竞赛级别")
    private String competitionLevel;

    /** 竞赛奖项 */
    @Excel(name = "竞赛奖项")
    private String awardLevel;

    /** 折合分数 */
    private Long scholarshipPoints;

    /** 获奖日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "获奖日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date awardDate;

    /** 证明材料（多个图片路径的JSON数组） */
    private String proofMaterial;

    /** 旧文件路径 */
    private List<String> existingProofMaterial;

    /** 审核状态 */
    private String auditStatus;

    /** 学生提交时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date applyTime;

    /** 审核人姓名 */
    private String nickName;

    /** 审核时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date auditTime;

    /** 审核意见 */
    private String auditRemark;

    /** 修读学期 */
    @Excel(name = "获奖学期")
    private String semester;

}

