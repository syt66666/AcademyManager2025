package com.ruoyi.system.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import java.util.Date;

public class StuLectureReportVo extends BaseEntity {
    /**
     * 主键id
     */
    private Integer reportId;
    /**
     * 学生学号
     */
    private String studentId;
    /**
     * 学生姓名
     */
    private String studentName;
    /**
     * 题目
     */
    private String reportTitle;
    /**
     * 报告人
     */
    private String reporter;
    /**
     * 报告时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date reportDate;
    /**
     * 报告提交时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date reportAdmitTime;
    /**
     * 内容简介
     */
    private String reportContent;
    /**
     * 链接
     */
    private String reportLink;
    /**
     *报告心得体会
     */
    private String reportFeeling;
    /**
     *报告心得体会文件名
     */
    private String reportFeelingName;
    /**
     * 报告地点
     */
    private String reportLocation;
    /**
     * 报告海报
     */
    private String lecturePoster;
    /**
     * 报告现场图片
     */
    private String reportPicture;
    /**
     * 审核状态
     */
    private Integer auditStatus ;
    /**
     *审核时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date auditTime;
    /**
     * 审核意见
     */
    private String auditRemark;
    /**
     * 审核人姓名
     */
    private String nickName;

    public Integer getReportId() {
        return reportId;
    }

    public void setReportId(Integer reportId) {
        this.reportId = reportId;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getReportTitle() {
        return reportTitle;
    }

    public void setReportTitle(String reportTitle) {
        this.reportTitle = reportTitle;
    }

    public String getReporter() {
        return reporter;
    }

    public void setReporter(String reporter) {
        this.reporter = reporter;
    }

    public Date getReportDate() {
        return reportDate;
    }

    public void setReportDate(Date reportDate) {
        this.reportDate = reportDate;
    }

    public Date getReportAdmitTime() {
        return reportAdmitTime;
    }

    public void setReportAdmitTime(Date reportAdmitTime) {
        this.reportAdmitTime = reportAdmitTime;
    }

    public String getReportContent() {
        return reportContent;
    }

    public void setReportContent(String reportContent) {
        this.reportContent = reportContent;
    }

    public String getReportLink() {
        return reportLink;
    }

    public void setReportLink(String reportLink) {
        this.reportLink = reportLink;
    }

    public String getReportFeeling() {
        return reportFeeling;
    }

    public void setReportFeeling(String reportFeeling) {
        this.reportFeeling = reportFeeling;
    }

    public String getReportFeelingName() {
        return reportFeelingName;
    }

    public void setReportFeelingName(String reportFeelingName) {
        this.reportFeelingName = reportFeelingName;
    }

    public String getReportLocation() {
        return reportLocation;
    }

    public void setReportLocation(String reportLocation) {
        this.reportLocation = reportLocation;
    }

    public String getLecturePoster() {
        return lecturePoster;
    }

    public void setLecturePoster(String lecturePoster) {
        this.lecturePoster = lecturePoster;
    }

    public String getReportPicture() {
        return reportPicture;
    }

    public void setReportPicture(String reportPicture) {
        this.reportPicture = reportPicture;
    }

    public Integer getAuditStatus() {
        return auditStatus;
    }

    public void setAuditStatus(Integer auditStatus) {
        this.auditStatus = auditStatus;
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

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
}
