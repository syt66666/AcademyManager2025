package com.ruoyi.system.domain;

import com.ruoyi.common.core.domain.BaseEntity;
import java.time.LocalDateTime;

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

    // ====== Getter and Setter ======

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Long getActivityId() {
        return activityId;
    }

    public void setActivityId(Long activityId) {
        this.activityId = activityId;
    }

    public LocalDateTime getBookAt() {
        return bookAt;
    }

    public void setBookAt(LocalDateTime bookAt) {
        this.bookAt = bookAt;
    }
}