package com.ruoyi.system.domain;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;


public class StuMentorshipRecord extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 记录ID
     */
    private Integer recordId;

    /**
     * 学生学号
     */
    @Excel(name = "学生学号")
    private String studentId;

    /**
     * 学生姓名
     */
    @Excel(name = "学生姓名")
    private String studentName;
    /**
     * 导师工号
     */
    @Excel(name = "导师工号")
    private String tutorId;

    /**
     * 导师姓名
     */
    @Excel(name = "导师姓名")
    private String tutorName;

    /**
     * 指导主题
     */
    @Excel(name = "指导主题")
    private String guidanceTopic;

    /**
     * 指导地点
     */
    @Excel(name = "指导地点")
    private String guidanceLocation;

    /**
     * 指导时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "指导时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date guidanceTime;

    /**
     * 总结文档路径
     */
    @Excel(name = "总结文档路径")
    private String summaryFilePath;

    /**
     * 照片路径数组
     */
    @Excel(name = "照片路径数组")
    private String photoPaths;

    /** 旧文件路径 */
    private List<String> existingProofMaterial;

    /**
     * 学期
     */
    @Excel(name = "学期")
    private String semester;

    /**
     * 提交时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "提交时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date submitTime;

    /**
     * 学生评价
     */
    @Excel(name = "学生评价")
    private String studentComment;

    /**
     * 审核状态
     */
    @Excel(name = "审核状态")
    private String auditStatus;

    /**
     * 审核时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "审核时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date auditTime;

    /**
     * 审核意见
     */
    @Excel(name = "审核意见")
    private String auditRemark;

    public List<String> getExistingProofMaterial() {
        return existingProofMaterial;
    }

    public void setExistingProofMaterial(List<String> existingProofMaterial) {
        this.existingProofMaterial = existingProofMaterial;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public void setRecordId(Integer recordId) {
        this.recordId = recordId;
    }

    public Integer getRecordId() {
        return recordId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setTutorId(String tutorId) {
        this.tutorId = tutorId;
    }

    public String getTutorId() {
        return tutorId;
    }

    public void setGuidanceTopic(String guidanceTopic) {
        this.guidanceTopic = guidanceTopic;
    }

    public String getGuidanceTopic() {
        return guidanceTopic;
    }

    public void setGuidanceLocation(String guidanceLocation) {
        this.guidanceLocation = guidanceLocation;
    }

    public String getGuidanceLocation() {
        return guidanceLocation;
    }

    public void setGuidanceTime(Date guidanceTime) {
        this.guidanceTime = guidanceTime;
    }

    public Date getGuidanceTime() {
        return guidanceTime;
    }

    public void setSummaryFilePath(String summaryFilePath) {
        this.summaryFilePath = summaryFilePath;
    }

    public String getSummaryFilePath() {
        return summaryFilePath;
    }

    public void setPhotoPaths(String photoPaths) {
        this.photoPaths = photoPaths;
    }

    public String getTutorName() {
        return tutorName;
    }

    public void setTutorName(String tutorName) {
        this.tutorName = tutorName;
    }

    public String getPhotoPaths() {
        return photoPaths;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public String getSemester() {
        return semester;
    }

    public void setSubmitTime(Date submitTime) {
        this.submitTime = submitTime;
    }

    public Date getSubmitTime() {
        return submitTime;
    }

    public void setStudentComment(String studentComment) {
        this.studentComment = studentComment;
    }

    public String getStudentComment() {
        return studentComment;
    }

    public void setAuditStatus(String auditStatus) {
        this.auditStatus = auditStatus;
    }

    public String getAuditStatus() {
        return auditStatus;
    }

    public void setAuditTime(Date auditTime) {
        this.auditTime = auditTime;
    }

    public Date getAuditTime() {
        return auditTime;
    }

    public void setAuditRemark(String auditRemark) {
        this.auditRemark = auditRemark;
    }

    public String getAuditRemark() {
        return auditRemark;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("recordId", getRecordId())
                .append("studentId", getStudentId())
                .append("tutorId", getTutorId())
                .append("guidanceTopic", getGuidanceTopic())
                .append("guidanceLocation", getGuidanceLocation())
                .append("guidanceTime", getGuidanceTime())
                .append("summaryFilePath", getSummaryFilePath())
                .append("photoPaths", getPhotoPaths())
                .append("semester", getSemester())
                .append("submitTime", getSubmitTime())
                .append("studentComment", getStudentComment())
                .append("auditStatus", getAuditStatus())
                .append("auditTime", getAuditTime())
                .append("tutorName", getTutorName())
                .append("auditRemark", getAuditRemark())
                .toString();
    }
}
