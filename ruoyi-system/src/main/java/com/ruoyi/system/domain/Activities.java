package com.ruoyi.system.domain;

import com.ruoyi.common.core.domain.BaseEntity;
import java.time.LocalDateTime;

/**
 * 活动实体类
 */
public class Activities extends BaseEntity {
    /** 主键id */
    private Long activityId;

    /** 活动名称 */
    private String activityName;

    /** 活动创建时间 */
    private LocalDateTime createdAt;

    /** 活动开始时间 */
    private LocalDateTime startTime;

    /** 活动结束时间 */
    private LocalDateTime endTime;

    /** 报名截止日期 */
    private LocalDateTime activityDeadline;

    /** 活动地点 */
    private String activityLocation;

    /** 活动容量 */
    private Integer activityCapacity;

    /** 活动主要内容 */
    private String activityDescription;

    /** 活动状态（upcoming/ongoing/ended） */
    private String status;

    /** 活动组织单位 */
    private String organizer;

    /** 注意事项 */
    private String notes;

    // ====== Getter and Setter ======

    public Long getActivityId() {
        return activityId;
    }

    public void setActivityId(Long activityId) {
        this.activityId = activityId;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public LocalDateTime getActivityDeadline() {
        return activityDeadline;
    }

    public void setActivityDeadline(LocalDateTime activityDeadline) {
        this.activityDeadline = activityDeadline;
    }

    public String getActivityLocation() {
        return activityLocation;
    }

    public void setActivityLocation(String activityLocation) {
        this.activityLocation = activityLocation;
    }

    public Integer getActivityCapacity() {
        return activityCapacity;
    }

    public void setActivityCapacity(Integer activityCapacity) {
        this.activityCapacity = activityCapacity;
    }

    public String getActivityDescription() {
        return activityDescription;
    }

    public void setActivityDescription(String activityDescription) {
        this.activityDescription = activityDescription;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOrganizer() {
        return organizer;
    }

    public void setOrganizer(String organizer) {
        this.organizer = organizer;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}