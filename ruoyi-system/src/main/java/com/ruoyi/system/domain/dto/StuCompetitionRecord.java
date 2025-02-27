package com.ruoyi.system.domain.dto;

import java.time.LocalDateTime;
import java.util.Date;

public class StuCompetitionRecord {
    private Integer competitionId; // 主键ID
    private String studentId; // 学号
    private String competitionName; // 竞赛名称
    private String competitionLevel; // 竞赛级别
    private String awardLevel; // 竞赛奖项
    private Integer scholarshipPoints; // 折合分数
    private Date competitionDate; // 竞赛参与日期
    private Date awardDate; // 获奖日期


    private byte[] proofMaterial; // 证明材料
    private String auditStatus; // 审核状态


    private LocalDateTime applyTime; // 提交时间
    private String nickName; // 审核人姓名
    private Date auditTime; // 审核时间
    private String auditRemark; // 审核意见
    private String semester; // 修读学期
    private String studentName;//学生姓名

    public Integer getCompetitionId() {
        return competitionId;
    }

    public void setCompetitionId(Integer competitionId) {
        this.competitionId = competitionId;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getCompetitionName() {
        return competitionName;
    }

    public void setCompetitionName(String competitionName) {
        this.competitionName = competitionName;
    }

    public String getCompetitionLevel() {
        return competitionLevel;
    }

    public void setCompetitionLevel(String competitionLevel) {
        this.competitionLevel = competitionLevel;
    }

    public String getAwardLevel() {
        return awardLevel;
    }

    public void setAwardLevel(String awardLevel) {
        this.awardLevel = awardLevel;
    }

    public Integer getScholarshipPoints() {
        return scholarshipPoints;
    }

    public void setScholarshipPoints(Integer scholarshipPoints) {
        this.scholarshipPoints = scholarshipPoints;
    }

    public Date getCompetitionDate() {
        return competitionDate;
    }

    public void setCompetitionDate(Date competitionDate) {
        this.competitionDate = competitionDate;
    }

    public Date getAwardDate() {
        return awardDate;
    }

    public void setAwardDate(Date awardDate) {
        this.awardDate = awardDate;
    }


    public String getAuditStatus() {
        return auditStatus;
    }

    public void setAuditStatus(String auditStatus) {
        this.auditStatus = auditStatus;
    }


    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public Date getAuditTime() {
        return auditTime;
    }

    public void setAuditTime(Date auditTime) {
        this.auditTime = auditTime;
    }

    public String getAuditRemark() {
        return auditRemark;
    }

    public void setAuditRemark(String auditRemark) {
        this.auditRemark = auditRemark;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }


    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }


    public byte[] getProofMaterial() {
        return proofMaterial;
    }

    public void setProofMaterial(byte[] proofMaterial) {
        this.proofMaterial = proofMaterial;
    }
    public LocalDateTime getApplyTime() {
        return applyTime;
    }

    public void setApplyTime(LocalDateTime applyTime) {
        this.applyTime = applyTime;
    }

}
