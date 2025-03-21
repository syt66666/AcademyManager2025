package com.ruoyi.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;


public class StuLectureReport extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 报告唯一编号 */
    private Integer reportId;

    /** 学生学号 */
    @Excel(name = "学生学号")
    private String studentId;

    /** 学生姓名 */
    @Excel(name = "学生姓名")
    private String studentName;

    /** 讲座题目 */
    @Excel(name = "讲座题目")
    private String reportTitle;

    /** 讲师姓名 */
    @Excel(name = "讲师姓名")
    private String reporter;

    /** 讲座日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "讲座日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date reportDate;

    /** 讲座内容简介 */
    private String reportContent;

    /** 讲座链接 */
    @Excel(name = "讲座链接")
    private String reportLink;

    /** 报告心得体会 */
    private String reportFeeling;

    /** 报告心得体会文件名 */
    private String reportFeelingName;

    /** 讲座海报 */
    private String lecturePoster;

    /** 报告现场图片 */
    private String reportPicture;

    /** 报告提交时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "报告提交时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date reportAdmitTime;

    /** 审核状态（0：未审核，1：已通过，2：未通过，3：未提交） */
    private Integer auditStatus;

    /** 报告地点 */
    private String reportLocation;

    /** 审核时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date auditTime;

    /** 审核意见 */
    private String auditRemark;

    /** 审核人姓名 */
    private String nickName;

    /** 原始字段 */
    private Integer semester;

    /** 导出用虚拟字段 */
    @Excel(name = "学期")
    private String semesterStr;

    private String auditStatusStr;

    public Integer getAuditStatus() {
        return auditStatus;
    }

    public void setAuditStatus(Integer auditStatus) {
        this.auditStatus = auditStatus;
        this.auditStatusStr=convertAuditStatus(auditStatus);
    }

    public Integer getSemester() {
        return semester;
    }

    public String getSemesterStr() {
        return semesterStr;
    }

    public String getAuditStatusStr() {
        return auditStatusStr;
    }

    public void setSemester(Integer semester) {
        this.semester = semester;
        this.semesterStr = convertSemester(semester);
    }

    /** JDK 8 兼容的转换方法 */
    private static String convertSemester(Integer semester) {
        if (semester == null) return "未知学期";

        switch (semester) {
            case 1: return "大一上";
            case 2: return "大一下";
            case 3: return "大二上";
            case 4: return "大二下";
            case 5: return "大三上";
            case 6: return "大三下";
            case 7: return "大四上";
            case 8: return "大四下";
            default: return "未知学期";
        }
    }
    private static String convertAuditStatus(Integer auditStatus) {
        if (auditStatus == null) return "未知状态";

        switch (auditStatus) {
            case 0: return "未审核";
            case 1: return "已通过";
            case 2: return "未通过";
            case 3: return "未提交";
            default: return "未知状态";
        }
    }

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


    public String getReportLocation() {
        return reportLocation;
    }

    public void setReportLocation(String reportLocation) {
        this.reportLocation = reportLocation;
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
