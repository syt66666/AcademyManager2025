package com.ruoyi.system.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import java.util.Date;

public class Activities extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 活动唯一ID */
    private Integer activityId;

    /** 活动名称 */
    @Excel(name = "活动名称")
    private String activityName;

    /** 活动开始时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "开始时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    /** 活动结束时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "结束时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    /** 活动地点 */
    @Excel(name = "活动地点")
    private String activityLocation;

    /** 活动容量 */
    @Excel(name = "活动容量")
    private Integer activityCapacity;

    /** 报名截止时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "报名截止时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date activityDeadline;

    /** 活动描述 */
    @Excel(name = "活动描述")
    private String activityDescription;

    /** 组织单位 */
    @Excel(name = "组织单位")
    private String organizer;

    /** 注意事项 */
    @Excel(name = "注意事项")
    private String notes;

    /** 活动状态 */
    @Excel(name = "活动状态", readConverterExp = "upcoming=未开始,ongoing=进行中,ended=已结束")
    private String status;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdAt;

    public void setActivityId(Integer activityId)
    {
        this.activityId = activityId;
    }

    public Integer getActivityId()
    {
        return activityId;
    }

    public void setActivityName(String activityName)
    {
        this.activityName = activityName;
    }

    public String getActivityName()
    {
        return activityName;
    }

    public void setStartTime(Date startTime)
    {
        this.startTime = startTime;
    }

    public Date getStartTime()
    {
        return startTime;
    }

    public void setEndTime(Date endTime)
    {
        this.endTime = endTime;
    }

    public Date getEndTime()
    {
        return endTime;
    }

    public void setActivityLocation(String activityLocation)
    {
        this.activityLocation = activityLocation;
    }

    public String getActivityLocation()
    {
        return activityLocation;
    }

    public void setActivityCapacity(Integer activityCapacity)
    {
        this.activityCapacity = activityCapacity;
    }

    public Integer getActivityCapacity()
    {
        return activityCapacity;
    }

    public void setActivityDeadline(Date activityDeadline)
    {
        this.activityDeadline = activityDeadline;
    }

    public Date getActivityDeadline()
    {
        return activityDeadline;
    }

    public void setActivityDescription(String activityDescription)
    {
        this.activityDescription = activityDescription;
    }

    public String getActivityDescription()
    {
        return activityDescription;
    }

    public void setOrganizer(String organizer)
    {
        this.organizer = organizer;
    }

    public String getOrganizer()
    {
        return organizer;
    }

    public void setNotes(String notes)
    {
        this.notes = notes;
    }

    public String getNotes()
    {
        return notes;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getStatus()
    {
        return status;
    }

    public void setCreatedAt(Date createdAt)
    {
        this.createdAt = createdAt;
    }

    public Date getCreatedAt()
    {
        return createdAt;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("activityId", getActivityId())
                .append("activityName", getActivityName())
                .append("startTime", getStartTime())
                .append("endTime", getEndTime())
                .append("activityLocation", getActivityLocation())
                .append("activityCapacity", getActivityCapacity())
                .append("activityDeadline", getActivityDeadline())
                .append("activityDescription", getActivityDescription())
                .append("organizer", getOrganizer())
                .append("notes", getNotes())
                .append("status", getStatus())
                .append("createdAt", getCreatedAt())
                .toString();
    }
}
