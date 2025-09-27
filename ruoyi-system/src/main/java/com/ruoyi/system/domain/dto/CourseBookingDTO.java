package com.ruoyi.system.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ruoyi.common.annotation.Excel;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
public class CourseBookingDTO {
    /** 主键id */
    private Long bookingId;

    /** 学生id（外键） */
    @Excel(name = "学生学号")
    private String studentId;
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
    /** 课程id（外键） */
    private Long courseId;
    /** 课程名称 */
    @Excel(name = "课程名称")
    private String courseName;
    /** 选课开始时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "选课开始时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date courseStart;

    /** 选课截止时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "选课截止时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date courseDeadline;

    /** 课程开始时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "课程开始时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    /** 课程结束时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "课程结束时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    /** 课程地点 */
    @Excel(name = "课程地点")
    private String courseLocation;
    /** 课程类型 */
    @Excel(name = "课程类型")
    private Long courseType;
    /** 组织单位 */
    @Excel(name = "组织单位")
    private String organizer;
    /** 课程描述 */
    private String courseDescription;
    /** 课程图片URL */
    private String pictureUrl;
    /** 注意事项 */
    private String notes;

    /** 选课时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "选课时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime bookAt;

    /** 证明材料 */
    @Excel(name = "证明材料")
    private String proof;

    /** 学习总结 */
    private String summary;

    /** 审核状态（待审核、未通过、已通过） */
    @Excel(name = "审核状态")
    private String status;

    /** 审核人 */
    private String reviewer;

    /** 审核意见 */
    private String reviewerComment;

    /** 审核时间 */
    private LocalDateTime reviewTime;

}


