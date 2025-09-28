package com.ruoyi.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 课程选课记录DTO对象（包含课程信息）
 *
 * @author ruoyi
 * @date 2025-09-25
 */
public class CourseBookingDTO extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 选课记录ID */
    private Long bookingId;

    /** 学生ID */
    @Excel(name = "学生ID")
    private String studentId;

    /** 学生姓名 */
    @Excel(name = "学生姓名")
    private String studentName;

    /** 所属书院 */
    @Excel(name = "所属书院")
    private String academy;

    /** 录取专业 */
    @Excel(name = "录取专业")
    private String major;

    /** 分流后系统内专业 */
    @Excel(name = "分流后系统内专业")
    private String systemMajor;

    /** 行政班 */
    @Excel(name = "行政班")
    private String studentClass;

    /** 学生性别 */
    @Excel(name = "学生性别")
    private String studentSex;

    /** 课程ID */
    @Excel(name = "课程ID")
    private Long courseId;

    /** 选课时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "选课时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date bookAt;

    /** 证明材料 */
    @Excel(name = "证明材料")
    private String proof;

    /** 学习总结 */
    @Excel(name = "学习总结")
    private String summary;

    /** 状态：待审核、已通过、未通过 */
    @Excel(name = "状态：待审核、已通过、未通过")
    private String status;

    /** 审核人 */
    @Excel(name = "审核人")
    private String reviewer;

    /** 审核意见 */
    @Excel(name = "审核意见")
    private String reviewerComment;

    /** 审核时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "审核时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date reviewTime;

    /** 组织单位 */
    @Excel(name = "组织单位")
    private String organizer;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date createdTime;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date updatedTime;

    // 课程信息字段
    /** 课程名称 */
    @Excel(name = "课程名称")
    private String courseName;

    /** 课程类型 */
    @Excel(name = "课程类型")
    private Long courseType;

    /** 课程分类 */
    @Excel(name = "课程分类")
    private String courseCategory;

    /** 选课开始时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "选课开始时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date courseStart;

    /** 选课截止时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "选课截止时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date courseDeadline;

    /** 课程地点 */
    @Excel(name = "课程地点")
    private String courseLocation;

    /** 课程开始时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "课程开始时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date startTime;

    /** 课程结束时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "课程结束时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date endTime;

    /** 课程描述 */
    @Excel(name = "课程描述")
    private String courseDescription;

    /** 课程图片URL */
    @Excel(name = "课程图片URL")
    private String pictureUrl;

    /** 注意事项 */
    @Excel(name = "注意事项")
    private String notes;

    /** 课程学分 */
    @Excel(name = "课程学分")
    private java.math.BigDecimal courseCredit;

    // 成绩相关字段
    /** 成绩值 */
    @Excel(name = "成绩值")
    private String scoreValue;

    /** 绩点 */
    @Excel(name = "绩点")
    private java.math.BigDecimal gpa;

    /** 成绩类型 */
    @Excel(name = "成绩类型")
    private String scoreType;

    /** 成绩上传时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "成绩上传时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date scoreUploadTime;

        // 学生信息字段
    /** 学生姓名 */
    @Excel(name = "学生姓名")
    private String studentName;

    /** 所属书院 */
    @Excel(name = "所属书院")
    private String college;


    // Getters and Setters
    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
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

    public String getAcademy() {
        return academy;
    }

    public void setAcademy(String academy) {
        this.academy = academy;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getSystemMajor() {
        return systemMajor;
    }

    public void setSystemMajor(String systemMajor) {
        this.systemMajor = systemMajor;
    }

    public String getStudentClass() {
        return studentClass;
    }

    public void setStudentClass(String studentClass) {
        this.studentClass = studentClass;
    }

    public String getStudentSex() {
        return studentSex;
    }

    public void setStudentSex(String studentSex) {
        this.studentSex = studentSex;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public Date getBookAt() {
        return bookAt;
    }

    public void setBookAt(Date bookAt) {
        this.bookAt = bookAt;
    }

    public String getProof() {
        return proof;
    }

    public void setProof(String proof) {
        this.proof = proof;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReviewer() {
        return reviewer;
    }

    public void setReviewer(String reviewer) {
        this.reviewer = reviewer;
    }

    public String getReviewerComment() {
        return reviewerComment;
    }

    public void setReviewerComment(String reviewerComment) {
        this.reviewerComment = reviewerComment;
    }

    public Date getReviewTime() {
        return reviewTime;
    }

    public void setReviewTime(Date reviewTime) {
        this.reviewTime = reviewTime;
    }

    public String getOrganizer() {
        return organizer;
    }

    public void setOrganizer(String organizer) {
        this.organizer = organizer;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public Date getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Date updatedTime) {
        this.updatedTime = updatedTime;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public Long getCourseType() {
        return courseType;
    }

    public void setCourseType(Long courseType) {
        this.courseType = courseType;
    }

    public String getCourseCategory() {
        return courseCategory;
    }

    public void setCourseCategory(String courseCategory) {
        this.courseCategory = courseCategory;
    }

    public Date getCourseStart() {
        return courseStart;
    }

    public void setCourseStart(Date courseStart) {
        this.courseStart = courseStart;
    }

    public Date getCourseDeadline() {
        return courseDeadline;
    }

    public void setCourseDeadline(Date courseDeadline) {
        this.courseDeadline = courseDeadline;
    }

    public String getCourseLocation() {
        return courseLocation;
    }

    public void setCourseLocation(String courseLocation) {
        this.courseLocation = courseLocation;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getCourseDescription() {
        return courseDescription;
    }

    public void setCourseDescription(String courseDescription) {
        this.courseDescription = courseDescription;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public java.math.BigDecimal getCourseCredit() {
        return courseCredit;
    }

    public void setCourseCredit(java.math.BigDecimal courseCredit) {
        this.courseCredit = courseCredit;
    }

    // 成绩相关字段的 getter 和 setter 方法
    public String getScoreValue() {
        return scoreValue;
    }

    public void setScoreValue(String scoreValue) {
        this.scoreValue = scoreValue;
    }

    public java.math.BigDecimal getGpa() {
        return gpa;
    }

    public void setGpa(java.math.BigDecimal gpa) {
        this.gpa = gpa;
    }

    public String getScoreType() {
        return scoreType;
    }

    public void setScoreType(String scoreType) {
        this.scoreType = scoreType;
    }

    public Date getScoreUploadTime() {
        return scoreUploadTime;
    }

    public void setScoreUploadTime(Date scoreUploadTime) {
        this.scoreUploadTime = scoreUploadTime;
    }
    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getCollege() {
        return college;
    }

    public void setCollege(String college) {
        this.college = college;
    }


    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("bookingId", getBookingId())
                .append("studentId", getStudentId())
                .append("courseId", getCourseId())
                .append("bookAt", getBookAt())
                .append("proof", getProof())
                .append("summary", getSummary())
                .append("status", getStatus())
                .append("reviewer", getReviewer())
                .append("reviewerComment", getReviewerComment())
                .append("reviewTime", getReviewTime())
                .append("organizer", getOrganizer())
                .append("createdTime", getCreatedTime())
                .append("updatedTime", getUpdatedTime())
                .append("courseName", getCourseName())
                .append("courseType", getCourseType())
                .append("courseLocation", getCourseLocation())
                .append("startTime", getStartTime())
                .append("endTime", getEndTime())
                .append("courseDescription", getCourseDescription())
                .append("pictureUrl", getPictureUrl())
                .append("organizer", getOrganizer())
                .append("notes", getNotes())
                .append("courseCredit", getCourseCredit())
                .append("courseCategory", getCourseCategory())
                .append("studentName", getStudentName())
                .append("college", getCollege())
                .toString();
    }
}
