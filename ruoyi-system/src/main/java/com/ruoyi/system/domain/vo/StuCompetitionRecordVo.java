package com.ruoyi.system.domain.vo;

import java.time.LocalDateTime;
import java.util.Date;

public class StuCompetitionRecordVo {
    private Integer competitionId; // 主键ID
    private String competitionName; // 竞赛名称
    private String competitionLevel; // 竞赛级别
    private String awardLevel; // 竞赛奖项
    private String auditStatus; // 审核状态
    private Date auditTime; // 审核时间
    private String auditRemark; // 审核意见

    // 获取主键ID
    public Integer getCompetitionId() {
        return competitionId;
    }

    // 设置主键ID
    public void setCompetitionId(Integer competitionId) {
        this.competitionId = competitionId;
    }

    // 获取竞赛名称
    public String getCompetitionName() {
        return competitionName;
    }

    // 设置竞赛名称
    public void setCompetitionName(String competitionName) {
        this.competitionName = competitionName;
    }

    // 获取竞赛级别
    public String getCompetitionLevel() {
        return competitionLevel;
    }

    // 设置竞赛级别
    public void setCompetitionLevel(String competitionLevel) {
        this.competitionLevel = competitionLevel;
    }

    // 获取竞赛奖项
    public String getAwardLevel() {
        return awardLevel;
    }

    // 设置竞赛奖项
    public void setAwardLevel(String awardLevel) {
        this.awardLevel = awardLevel;
    }

    // 获取审核状态
    public String getAuditStatus() {
        return auditStatus;
    }

    // 设置审核状态
    public void setAuditStatus(String auditStatus) {
        this.auditStatus = auditStatus;
    }

    // 获取审核时间
    public Date getAuditTime() {
        return auditTime;
    }

    // 设置审核时间
    public void setAuditTime(Date auditTime) {
        this.auditTime = auditTime;
    }

    // 获取审核意见
    public String getAuditRemark() {
        return auditRemark;
    }

    // 设置审核意见
    public void setAuditRemark(String auditRemark) {
        this.auditRemark = auditRemark;
    }
}
