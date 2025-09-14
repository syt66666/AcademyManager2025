package com.ruoyi.system.domain.dto;

import com.ruoyi.common.annotation.Excel;
import lombok.Data;

/**
 * 预约学生导出DTO
 * 只包含预约活动学生列表中显示的字段
 */
@Data
public class BookingExportDTO {
    
    /** 活动ID（用于查询条件，不导出） */
    private Long activityId;
    
    /** 学生学号 */
    @Excel(name = "学号")
    private String studentId;
    
    /** 学生姓名 */
    @Excel(name = "姓名")
    private String studentName;
    
    /** 所属书院 */
    @Excel(name = "所属书院")
    private String academy;
    
    /** 审核状态 */
    @Excel(name = "审核状态")
    private String status;
}
