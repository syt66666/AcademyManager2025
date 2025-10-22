package com.ruoyi.system.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.Date;
@EqualsAndHashCode(callSuper = true)
@Data
public class Activities extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 活动唯一ID */
    private Integer activityId;

    /** 活动名称 */
    @Excel(name = "活动名称")
    private String activityName;

    /** 报名开始时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "报名开始时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date activityStart;

    /** 报名截止时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "报名截止时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date activityDeadline;

    /** 活动开始时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "活动开始时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    /** 活动结束时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "活动结束时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    /** 活动地点 */
    @Excel(name = "活动地点")
    private String activityLocation;

    /** 活动当前已选人数 */
    private Integer activityCapacity;

    /** 组织单位 */
    @Excel(name = "组织单位")
    private String organizer;

    /** 活动总容量 */
    @Excel(name = "活动总容量")
    private Integer activityTotalCapacity;

    /** 活动描述 */
    @Excel(name = "活动描述")
    private String activityDescription;

    /** 注意事项 */
    @Excel(name = "注意事项")
    private String notes;

    /** 活动类型 */
    @Excel(name = "活动类型")
    @NotBlank(message = "活动类型不能为空")
    private String activityType;

    /** 活动状态 */
    private String status;

    /** 活动图片URL */
    @Excel(name = "活动图片")
    private String pictureUrl;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdAt;

    //乐观锁控制
    private Integer version;

}
