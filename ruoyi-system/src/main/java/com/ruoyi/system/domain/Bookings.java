package com.ruoyi.system.domain;

import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.system.utils.JpaJsonConverter;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 预约实体类
 */
public class Bookings extends BaseEntity {
    /** 主键id */
    private Long bookingId;

    /** 学生id（外键） */
    private Long studentId;

    /** 活动id（外键） */
    private Long activityId;

    /** 预约时间 */
    private LocalDateTime bookAt;
    /** 证明材料 */
    //private String proof;
    @Column(columnDefinition = "json")
    @Convert(converter = JpaJsonConverter.class)
    private List<String> proof;

    /** 学生总结 */
    private String summary;

    /** 预约状态（未提交、未通过、未审核、已通过） */
    private String status; // 正确的字段

    /** 审核人 */
    private String reviewer;

    /** 审核意见 */
    private String reviewComment;

    /** 审核时间 */
    private LocalDateTime reviewTime;

    // getter/setter
    public Long getBookingId() { return bookingId; }
    public void setBookingId(Long bookingId) { this.bookingId = bookingId; }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public Long getActivityId() { return activityId; }
    public void setActivityId(Long activityId) { this.activityId = activityId; }

    public LocalDateTime getBookAt() { return bookAt; }
    public void setBookAt(LocalDateTime bookAt) { this.bookAt = bookAt; }

    /*public String getProof() { return proof; }
    public void setProof(String proof) { this.proof = proof; }*/

    public List<String> getProof() {
        return proof;
    }

    public void setProof(List<String> proof) {
        this.proof = proof;
    }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getReviewer() { return reviewer; }
    public void setReviewer(String reviewer) { this.reviewer = reviewer; }

    public String getReviewComment() { return reviewComment; }
    public void setReviewComment(String reviewComment) { this.reviewComment = reviewComment; }

    public LocalDateTime getReviewTime() { return reviewTime; }
    public void setReviewTime(LocalDateTime reviewTime) { this.reviewTime = reviewTime; }
}