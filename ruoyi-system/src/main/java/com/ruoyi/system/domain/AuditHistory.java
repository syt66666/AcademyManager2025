package com.ruoyi.system.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;

import java.util.Date;

@Data
public class AuditHistory extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** 模块类型（1科创竞赛 2文体活动 3讲座报告 4导师指导） */
    private Integer moduleType;

    /** 模块记录ID */
    private Long moduleId;

    /** 审核动作（通过/拒绝/撤回） */
    private String auditAction;

    /** 审核前状态 */
    private String auditStatusBefore;

    /** 审核后状态 */
    private String auditStatusAfter;

    /** 审核人ID */
    private String auditorId;

    /** 审核时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date auditTime;

    /** 审核意见 */
    private String auditRemark;

    /** IP地址 */
    private String ipAddress;

    /** 设备信息 */
    private String deviceInfo;
}