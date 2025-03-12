package com.ruoyi.system.domain;



import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 学生讲座报告实体类
 */
@Data
public class StudentLectureReport extends BaseEntity {
    /**
     * 学生学号
     */
    private String studentId;
    /**
     * 学生姓名
     */
    private String studentName;
    /**
     * 主键id
     */
    private int reportId;
    /**
     * 题目
     */
    private String reportTitle;
    /**
     * 报告人
     */
    private String reporter;
    /**
     * 报告时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime reportDate;
    /**
     * 报告提交时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime reportAdmitTime;
    /**
     * 内容简介
     */
    private String reportContent;
    /**
     * 链接
     */
    private String reportLink;
    /**
     *报告心得体会
     */
    private String reportFeeling;
    /**
     * 报告海报
     */
    private String lecturePoster;
    /**
     * 报告现场图片
     */
    private String reportPicture;
    /**
     * 审核状态
     */
    private int auditStatus = 0;
    /**
     *审核时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime auditTime;
    /**
     * 审核意见
     */
    private String auditRemark;
    /**
     * 审核人姓名
     */
    private String nickName;
    /**
     * 上传学期
     */
    private int semester;


}
