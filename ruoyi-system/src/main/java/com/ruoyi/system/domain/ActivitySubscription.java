package com.ruoyi.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 用户活动订阅对象 activity_subscription
 *
 * @author ruoyi
 * @date 2025-07-17
 */
public class ActivitySubscription extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 订阅ID */
    private Long subscriptionId;

    /** 学号 */
    @Excel(name = "学号")
    private String studentId;

    /** 活动ID */
    @Excel(name = "活动ID")
    private Long activityId;

    /** 订阅时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "订阅时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date subscribedAt;

    /** 取消订阅时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "取消订阅时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date unsubscribedAt;

    public void setSubscriptionId(Long subscriptionId)
    {
        this.subscriptionId = subscriptionId;
    }

    public Long getSubscriptionId()
    {
        return subscriptionId;
    }
    public void setStudentId(String studentId)
    {
        this.studentId = studentId;
    }

    public String getStudentId()
    {
        return studentId;
    }
    public void setActivityId(Long activityId)
    {
        this.activityId = activityId;
    }

    public Long getActivityId()
    {
        return activityId;
    }
    public void setSubscribedAt(Date subscribedAt)
    {
        this.subscribedAt = subscribedAt;
    }

    public Date getSubscribedAt()
    {
        return subscribedAt;
    }
    public void setUnsubscribedAt(Date unsubscribedAt)
    {
        this.unsubscribedAt = unsubscribedAt;
    }

    public Date getUnsubscribedAt()
    {
        return unsubscribedAt;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("subscriptionId", getSubscriptionId())
                .append("studentId", getStudentId())
                .append("activityId", getActivityId())
                .append("subscribedAt", getSubscribedAt())
                .append("unsubscribedAt", getUnsubscribedAt())
                .toString();
    }
}
