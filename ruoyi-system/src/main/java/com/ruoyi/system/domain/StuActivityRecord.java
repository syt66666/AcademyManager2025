package com.ruoyi.system.domain;

import java.util.Date;

public class StuActivityRecord {
    // 活动ID
    private Integer activityId;
    // 学生ID
    private String studentId;
    // 活动名称
    private String activityName;
    // 活动等级
    private String activityLevel;
    // 奖励等级
    private String awardLevel;
    // 奖学金积分
    private Integer scholarshipPoints;
    // 奖励日期
    private Date awardDate;
    // 证明材料
    private String proofMaterial;
    // 学期
    private Integer semester;
    // 申请时间
    private Date applyTime;
    // 昵称
    private String nickName;
    // 审核状态
    private String auditStatus;
    // 审核时间
    private Date auditTime;
    // 审核备注
    private String auditRemark;


    // 获取审核备注
    public String getAuditRemark() {
        return auditRemark;
    }

    // 设置审核备注
    public void setAuditRemark(String auditRemark) {
        this.auditRemark = auditRemark;
    }

    // 获取审核时间
    public Date getAuditTime() {
        return auditTime;
    }

    // 设置审核时间
    public void setAuditTime(Date auditTime) {
        this.auditTime = auditTime;
    }

    // 获取审核状态
    public String getAuditStatus() {
        return auditStatus;
    }

    // 设置审核状态
    public void setAuditStatus(String auditStatus) {
        this.auditStatus = auditStatus;
    }

    // 获取昵称
    public String getNickName() {
        return nickName;
    }

    // 设置昵称
    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    // 获取申请时间
    public Date getApplyTime() {
        return applyTime;
    }

    // 设置申请时间
    public void setApplyTime(Date applyTime) {
        this.applyTime = applyTime;
    }

    // 获取学期
    public Integer getSemester() {
        return semester;
    }

    // 设置学期
    public void setSemester(Integer semester) {
        this.semester = semester;
    }

    // 获取证明材料
    public String getProofMaterial() {
        return proofMaterial;
    }

    // 设置证明材料
    public void setProofMaterial(String proofMaterial) {
        this.proofMaterial = proofMaterial;
    }

    // 获取奖励日期
    public Date getAwardDate() {
        return awardDate;
    }

    // 设置奖励日期
    public void setAwardDate(Date awardDate) {
        this.awardDate = awardDate;
    }

    // 获取奖学金积分
    public Integer getScholarshipPoints() {
        return scholarshipPoints;
    }

    // 设置奖学金积分
    public void setScholarshipPoints(Integer scholarshipPoints) {
        this.scholarshipPoints = scholarshipPoints;
    }

    // 获取奖励等级
    public String getAwardLevel() {
        return awardLevel;
    }

    // 设置奖励等级
    public void setAwardLevel(String awardLevel) {
        this.awardLevel = awardLevel;
    }

    // 获取活动等级
    public String getActivityLevel() {
        return activityLevel;
    }

    // 设置活动等级
    public void setActivityLevel(String activityLevel) {
        this.activityLevel = activityLevel;
    }

    // 获取活动名称
    public String getActivityName() {
        return activityName;
    }

    // 设置活动名称
    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    // 获取学生ID
    public String getStudentId() {
        return studentId;
    }

    // 设置学生ID
    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    // 获取活动ID
    public Integer getActivityId() {
        return activityId;
    }

    // 设置活动ID
    public void setActivityId(Integer activityId) {
        this.activityId = activityId;
    }



}

