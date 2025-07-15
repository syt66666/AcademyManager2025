package com.ruoyi.system.domain;

import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.system.utils.JpaJsonConverter;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 预约实体类
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class Bookings extends BaseEntity {
    /** 主键id */
    private Long bookingId;

    /** 学生id（外键） */
    private String studentId;

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

}