package com.ruoyi.system.domain;

import java.text.SimpleDateFormat;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;


public class StuActivityRecord extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private String awardDateBegin;

    private String awardDateEnd;

    /** 主键ID */
    private Integer activityId;

    /** 学号 */
    @Excel(name = "学号")
    private String studentId;

    /** 姓名 */
    @Excel(name = "姓名")
    private String studentName;

    /** 活动名称 */
    @Excel(name = "活动名称")
    private String activityName;

    /** 活动级别 */
    @Excel(name = "获奖级别")
    private String activityLevel;

    /** 活动奖项 */
    @Excel(name = "获奖奖项")
    private String awardLevel;

    /** 折合奖学金分数 */
    private Long scholarshipPoints;

    /** 获奖日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "获奖日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date awardDate;

    /** 证明材料路径 */
    private String proofMaterial;

    /** 修读学期 */
    @Excel(name = "修读学期")
    private String semester;

    /** 提交时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date applyTime;

    /** 审核人姓名 */
    private String nickName;

    /** 审核状态 */
    @Excel(name = "审核状态")
    private String auditStatus;

    /** 审核时间 */
//    @Excel(name = "审核时间", width = 30, dateFormat = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date auditTime;

    /** 审核意见 */
    private String auditRemark;

    public String getAwardDateBegin() {
        return awardDateBegin;
    }

    public void setAwardDateBegin(String awardDateBegin) {
        this.awardDateBegin = awardDateBegin;
    }

    public String getAwardDateEnd() {
        return awardDateEnd;
    }

    public void setAwardDateEnd(String awardDateEnd) {
        this.awardDateEnd = awardDateEnd;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public void setActivityId(Integer activityId)
    {
        this.activityId = activityId;
    }

    public Integer getActivityId()
    {
        return activityId;
    }
    public void setStudentId(String studentId)
    {
        this.studentId = studentId;
    }

    public String getStudentId()
    {
        return studentId;
    }
    public void setActivityName(String activityName)
    {
        this.activityName = activityName;
    }

    public String getActivityName()
    {
        return activityName;
    }
    public void setActivityLevel(String activityLevel)
    {
        this.activityLevel = activityLevel;
    }

    public String getActivityLevel()
    {
        return activityLevel;
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
    public void setSemester(String semester)
    {
        this.semester = semester;
    }

    public String getSemester()
    {
        return semester;
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
    public void setAuditStatus(String auditStatus)
    {
        this.auditStatus = auditStatus;
    }

    public String getAuditStatus()
    {
        return auditStatus;
    }
    public void setAuditTime(Date auditTime) { this.auditTime = auditTime; }

    public Date getAuditTime()
    {
        return auditTime;
    }
    public void setAuditRemark(String auditRemark) { this.auditRemark = auditRemark; }

    public String getAuditRemark()
    {
        return auditRemark;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("activityId", getActivityId())
                .append("studentId", getStudentId())
                .append("activityName", getActivityName())
                .append("activityLevel", getActivityLevel())
                .append("awardLevel", getAwardLevel())
                .append("scholarshipPoints", getScholarshipPoints())
                .append("awardDate", getAwardDate())
                .append("proofMaterial", getProofMaterial())
                .append("semester", getSemester())
                .append("applyTime", getApplyTime())
                .append("nickName", getNickName())
                .append("auditStatus", getAuditStatus())
                .append("auditTime", getAuditTime())
                .append("auditRemark", getAuditRemark())
                .toString();
    }
}