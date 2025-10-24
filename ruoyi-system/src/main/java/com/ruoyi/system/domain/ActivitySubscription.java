package com.ruoyi.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

@EqualsAndHashCode(callSuper = true)
@Data
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

    /** 活动当前已选人数 */
    @Excel(name = "已选人数")
    private Integer activityCapacity;
    /** 报名开始时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "报名开始时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date activityStart;
    /** 报名截止时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "报名截止时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date activityDeadline;

    /** 组织单位 */
    @Excel(name = "组织单位")
    private String organizer;
    /** 活动描述 */
    @Excel(name = "活动描述")
    private String activityDescription;
    /** 注意事项 */
    @Excel(name = "注意事项")
    private String notes;

    /** 活动状态 */
    private String status;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdAt;

    //乐观锁控制
    private Integer version;

    /** 活动总容量 */
    @Excel(name = "活动总容量")
    private Integer activityTotalCapacity;
}
