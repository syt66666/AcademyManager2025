package com.ruoyi.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

public class StudentLectureReport extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 报告唯一编号 */
    private Long reportId;

    /** 学生学号 */
    @Excel(name = "学生学号")
    private String studentId;

    /** 学生姓名 */
    @Excel(name = "学生姓名")
    private String studentName;

    /** 题目 */
    @Excel(name = "题目")
    private String reportTitle;

    /** 报告人 */
    @Excel(name = "报告人")
    private String reporter;

    /** 报告时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "报告时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date reportDate;

    /** 报告内容简介 */
    @Excel(name = "报告内容简介")
    private String reportContent;

    /** 报告链接 */
    @Excel(name = "报告链接")
    private String reportLink;

    /** 报告心得体会 */
    @Excel(name = "报告心得体会")
    private String reportFeeling;

    /** 演讲海报 */
    @Excel(name = "演讲海报")
    private String lecturePoster;

    /** 报告现场图片 */
    @Excel(name = "报告现场图片")
    private String reportPicture;

    /** 报告提交时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "报告提交时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date reportAdmitTime;

    /** 审核状态（0：未审核，1：已通过，2：未通过，3：未提交） */
    private Integer auditStatus;

    /** 审核时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date auditTime;

    /** 审核意见 */
    private String auditRemark;

    /** 审核人姓名 */
    private String nickName;

    /** 上传学期（1.2.3.4.5.6.7.8） */
    private Integer semester;

    public Long getReportId() {
        return reportId;
    }

    public void setReportId(Long reportId) {
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

    public Date getReportAdmitTime() {
        return reportAdmitTime;
    }

    public void setReportAdmitTime(Date reportAdmitTime) {
        this.reportAdmitTime = reportAdmitTime;
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

    public Integer getSemester() {
        return semester;
    }

    public void setSemester(Integer semester) {
        this.semester = semester;
    }
}
