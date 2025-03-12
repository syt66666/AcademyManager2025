package com.ruoyi.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;


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
    @Excel(name = "证明材料", readConverterExp = "多=个图片路径的JSON数组")
    private String proofMaterial;

    /** 审核状态 */
    private String auditStatus;

    /** 学生提交时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "学生提交时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date applyTime;

    /** 审核人姓名 */
    private String nickName;

    /** 审核时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "审核时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date auditTime;

    /** 审核意见 */
    private String auditRemark;

    /** 修读学期 */
    private String semester;

    public String getAwardDateEnd() {
        return awardDateEnd;
    }

    public void setAwardDateEnd(String awardDateEnd) {
        this.awardDateEnd = awardDateEnd;
    }

    public String getAwardDateBegin() {
        return awardDateBegin;
    }

    public void setAwardDateBegin(String awardDateBegin) {
        this.awardDateBegin = awardDateBegin;
    }

    public void setCompetitionId(Integer competitionId)
    {
        this.competitionId = competitionId;
    }

    public Integer getCompetitionId()
    {
        return competitionId;
    }
    public void setStudentId(String studentId)
    {
        this.studentId = studentId;
    }

    public String getStudentId()
    {
        return studentId;
    }
    public void setCompetitionName(String competitionName)
    {
        this.competitionName = competitionName;
    }

    public String getCompetitionName()
    {
        return competitionName;
    }
    public void setCompetitionLevel(String competitionLevel)
    {
        this.competitionLevel = competitionLevel;
    }

    public String getCompetitionLevel()
    {
        return competitionLevel;
    }
    public void setAwardLevel(String awardLevel)
    {
        this.awardLevel = awardLevel;
    }

    public String getAwardLevel()
    {
        return awardLevel;
    }
    public void setScholarshipPoints(Long scholarshipPoints)
    {
        this.scholarshipPoints = scholarshipPoints;
    }

    public Long getScholarshipPoints()
    {
        return scholarshipPoints;
    }
    public void setAwardDate(Date awardDate)
    {
        this.awardDate = awardDate;
    }

    public Date getAwardDate()
    {
        return awardDate;
    }
    public void setProofMaterial(String proofMaterial)
    {
        this.proofMaterial = proofMaterial;
    }

    public String getProofMaterial()
    {
        return proofMaterial;
    }
    public void setAuditStatus(String auditStatus)
    {
        this.auditStatus = auditStatus;
    }

    public String getAuditStatus()
    {
        return auditStatus;
    }
    public void setApplyTime(Date applyTime)
    {
        this.applyTime = applyTime;
    }

    public Date getApplyTime()
    {
        return applyTime;
    }
    public void setNickName(String nickName)
    {
        this.nickName = nickName;
    }

    public String getNickName()
    {
        return nickName;
    }
    public void setAuditTime(Date auditTime)
    {
        this.auditTime = auditTime;
    }

    public Date getAuditTime()
    {
        return auditTime;
    }
    public void setAuditRemark(String auditRemark)
    {
        this.auditRemark = auditRemark;
    }

    public String getAuditRemark()
    {
        return auditRemark;
    }
    public void setSemester(String semester)
    {
        this.semester = semester;
    }

    public String getSemester()
    {
        return semester;
    }
    public void setStudentName(String studentName)
    {
        this.studentName = studentName;
    }

    public String getStudentName()
    {
        return studentName;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("competitionId", getCompetitionId())
                .append("studentId", getStudentId())
                .append("competitionName", getCompetitionName())
                .append("competitionLevel", getCompetitionLevel())
                .append("awardLevel", getAwardLevel())
                .append("scholarshipPoints", getScholarshipPoints())
                .append("awardDate", getAwardDate())
                .append("proofMaterial", getProofMaterial())
                .append("auditStatus", getAuditStatus())
                .append("applyTime", getApplyTime())
                .append("nickName", getNickName())
                .append("auditTime", getAuditTime())
                .append("auditRemark", getAuditRemark())
                .append("semester", getSemester())
                .append("studentName", getStudentName())
                .toString();
    }
}

