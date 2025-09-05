package com.ruoyi.system.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ruoyi.common.annotation.Excel;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class BookingDTO {
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
    /** 活动id（外键） */
    private Long activityId;
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
    /** 活动类型 */
    @Excel(name = "活动类型")
    private String activityType;
    /** 组织单位 */
    @Excel(name = "组织单位")
    private String organizer;
    /** 活动描述 */
    private String activityDescription;
    /** 注意事项 */
    private String notes;

    /** 预约时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "预约时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime bookAt;

    /** 证明材料 */
    @JsonProperty("proof")
    private List<ProofItem> proof = new ArrayList<>(); // 使用具体类型而非Object

    // 嵌套的ProofItem类
    public static class ProofItem {
        private String name;
        private String url;

        // 必须有无参构造函数
        public ProofItem() {}

        // getters和setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getUrl() { return url; }
        public void setUrl(String url) { this.url = url; }
    }

    /** 学生总结 */
    private String summary;

    /** 审核状态（未提交、未通过、未审核、已通过） */
    @Excel(name = "审核状态")
    private String status;

    /** 审核人 */
    private String reviewer;

    /** 审核意见 */
    private String reviewComment;

    /** 审核时间 */
    private LocalDateTime reviewTime;

}
