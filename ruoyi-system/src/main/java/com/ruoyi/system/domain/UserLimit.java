package com.ruoyi.system.domain;

import java.util.Date;

public class UserLimit {
    private Long id;
    private String studentId;
    private Long activityId;
    private Integer isCancel;
    private Date createdAt;

    // 构造方法
    public UserLimit() {}

    public UserLimit(String studentId, Long activityId, Integer isCancel, Date createdAt) {
        this.studentId = studentId;
        this.activityId = activityId;
        this.isCancel = isCancel;
        this.createdAt = createdAt;
    }

    // Getter和Setter方法
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public Long getActivityId() {
        return activityId;
    }

    public void setActivityId(Long activityId) {
        this.activityId = activityId;
    }

    public Integer getIsCancel() {
        return isCancel;
    }

    public void setIsCancel(Integer isCancel) {
        this.isCancel = isCancel;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "UserLimit{" +
                "id=" + id +
                ", studentId='" + studentId + '\'' +
                ", activityId=" + activityId +
                ", isCancel=" + isCancel +
                ", createdAt=" + createdAt +
                '}';
    }
}