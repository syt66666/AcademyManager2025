package com.ruoyi.system.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

import java.util.Date;

/**
 * 预约实体类
 */
public class Bookings extends BaseEntity {
    /** 主键id */
    private Long bookingId;

    /** 学生id（外键） */
    @Excel(name = "学生ID")
    private Long studentId;

    /** 活动id（外键） */
    @Excel(name = "活动ID")
    private Long activityId;

    /** 预约时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "预约时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date bookAt;

    /** 证明材料 */
    @Excel(name = "证明材料")
    private String proof;

    /** 学生总结 */
    @Excel(name = "学生总结")
    private String summary;

    /** 预约状态（未提交、未通过、未审核、已通过） */
    @Excel(name = "预约状态")
    private String status;

    /** 审核人 */
    @Excel(name = "审核人")
    private String reviewer;

    /** 审核意见 */
    @Excel(name = "审核意见")
    private String reviewComment;

    /** 审核时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "审核时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date reviewTime;

    // getter/setter
    public Long getBookingId() { return bookingId; }
    public void setBookingId(Long bookingId) { this.bookingId = bookingId; }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public Long getActivityId() { return activityId; }
    public void setActivityId(Long activityId) { this.activityId = activityId; }

    public Date getBookAt() { return bookAt; }
    public void setBookAt(Date bookAt) { this.bookAt = bookAt; }

    public String getProof() { return proof; }
    public void setProof(String proof) { this.proof = proof; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getReviewer() { return reviewer; }
    public void setReviewer(String reviewer) { this.reviewer = reviewer; }

    public String getReviewComment() { return reviewComment; }
    public void setReviewComment(String reviewComment) { this.reviewComment = reviewComment; }

    public Date getReviewTime() { return reviewTime; }
    public void setReviewTime(Date reviewTime) { this.reviewTime = reviewTime; }
}
