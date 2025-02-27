package com.ruoyi.system.domain;

import java.util.Date;

public class StuActivityRecord {
    private Integer activityId;
    private String studentId;
    private String activityName;
    private String activityLevel;
    private String awardLevel;
    private Integer scholarshipPoints;
    private Date awardDate;
    private String proofMaterial;
    private Integer semester;
    private Date applyTime;
    private String nickName;
    private String auditStatus;
    private Date auditTime;
    private String auditRemark;


    public String getAuditRemark() {
        return auditRemark;
    }

    public void setAuditRemark(String auditRemark) {
        this.auditRemark = auditRemark;
    }

    public Date getAuditTime() {
        return auditTime;
    }

    public void setAuditTime(Date auditTime) {
        this.auditTime = auditTime;
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

    public Date getApplyTime() {
        return applyTime;
    }

    public void setApplyTime(Date applyTime) {
        this.applyTime = applyTime;
    }

    public Integer getSemester() {
        return semester;
    }

    public void setSemester(Integer semester) {
        this.semester = semester;
    }

    public String getProofMaterial() {
        return proofMaterial;
    }

    public void setProofMaterial(String proofMaterial) {
        this.proofMaterial = proofMaterial;
    }

    public Date getAwardDate() {
        return awardDate;
    }

    public void setAwardDate(Date awardDate) {
        this.awardDate = awardDate;
    }

    public Integer getScholarshipPoints() {
        return scholarshipPoints;
    }

    public void setScholarshipPoints(Integer scholarshipPoints) {
        this.scholarshipPoints = scholarshipPoints;
    }

    public String getAwardLevel() {
        return awardLevel;
    }

    public void setAwardLevel(String awardLevel) {
        this.awardLevel = awardLevel;
    }

    public String getActivityLevel() {
        return activityLevel;
    }

    public void setActivityLevel(String activityLevel) {
        this.activityLevel = activityLevel;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public Integer getActivityId() {
        return activityId;
    }

    public void setActivityId(Integer activityId) {
        this.activityId = activityId;
    }



}

