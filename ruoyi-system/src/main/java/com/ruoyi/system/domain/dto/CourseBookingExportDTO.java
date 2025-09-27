package com.ruoyi.system.domain.dto;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import lombok.Data;

/**
 * 课程选课记录导出DTO
 * 用于课程选课记录的导出功能
 *
 * @author ruoyi
 * @date 2025-01-27
 */
@Data
public class CourseBookingExportDTO
{
    /** 选课记录ID */
    private Long bookingId;

    /** 学生ID */
    @Excel(name = "学生ID")
    private String studentId;

    /** 学生姓名 */
    @Excel(name = "学生姓名")
    private String studentName;

    /** 所属书院 */
    @Excel(name = "所属书院")
    private String academy;

    /** 录取专业 */
    @Excel(name = "录取专业")
    private String major;

    /** 分流后系统内专业 */
    @Excel(name = "分流后系统内专业")
    private String systemMajor;

    /** 行政班 */
    @Excel(name = "行政班")
    private String studentClass;

    /** 学生性别 */
    @Excel(name = "学生性别")
    private String studentSex;

    /** 课程ID */
    @Excel(name = "课程ID")
    private Long courseId;

    /** 课程名称 */
    @Excel(name = "课程名称")
    private String courseName;

    /** 课程类型 */
    @Excel(name = "课程类型")
    private Long courseType;

    /** 课程分类 */
    @Excel(name = "课程分类")
    private String courseCategory;

    /** 选课时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "选课时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date bookAt;

    /** 证明材料 */
    @Excel(name = "证明材料")
    private String proof;

    /** 学习总结 */
    @Excel(name = "学习总结")
    private String summary;

    /** 状态：待审核、已通过、未通过 */
    @Excel(name = "状态：待审核、已通过、未通过")
    private String status;

    /** 审核人 */
    @Excel(name = "审核人")
    private String reviewer;

    /** 审核意见 */
    @Excel(name = "审核意见")
    private String reviewerComment;

    /** 审核时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "审核时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date reviewTime;

    /** 组织单位 */
    @Excel(name = "组织单位")
    private String organizer;

    /** 选课开始时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "选课开始时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date courseStart;

    /** 选课截止时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "选课截止时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date courseDeadline;

    /** 课程地点 */
    @Excel(name = "课程地点")
    private String courseLocation;

    /** 课程开始时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "课程开始时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date startTime;

    /** 课程结束时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "课程结束时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date endTime;

    /** 课程描述 */
    @Excel(name = "课程描述")
    private String courseDescription;

    /** 课程图片URL */
    @Excel(name = "课程图片URL")
    private String pictureUrl;

    /** 注意事项 */
    @Excel(name = "注意事项")
    private String notes;

    /** 课程学分 */
    @Excel(name = "课程学分")
    private java.math.BigDecimal courseCredit;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date createdTime;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date updatedTime;
}
