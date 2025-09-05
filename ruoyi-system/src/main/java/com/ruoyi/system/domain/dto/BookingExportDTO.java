package com.ruoyi.system.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import lombok.Data;

import java.time.LocalDateTime;

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
    
    /** 专业（优先显示录取专业，其次显示系统内专业） */
    @Excel(name = "专业")
    private String major;
    
    /** 行政班 */
    @Excel(name = "行政班")
    private String studentClass;
    
    /** 审核状态 */
    @Excel(name = "审核状态")
    private String status;
    
    /** 预约时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "预约时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime bookAt;
}
