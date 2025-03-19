package com.ruoyi.system.domain;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

@Data
public class StuMentorshipRecord extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 记录ID
     */
    private Integer recordId;

    /**
     * 学生学号
     */
    @Excel(name = "学生学号")
    private String studentId;

    /**
     * 学生姓名
     */
    @Excel(name = "学生姓名")
    private String studentName;
    /**
     * 导师工号
     */
    @Excel(name = "导师工号")
    private String tutorId;

    /**
     * 导师姓名
     */
    @Excel(name = "导师姓名")
    private String tutorName;

    /**
     * 指导主题
     */
    @Excel(name = "指导主题")
    private String guidanceTopic;

    /**
     * 指导地点
     */
    @Excel(name = "指导地点")
    private String guidanceLocation;

    /**
     * 指导时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "指导时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date guidanceTime;

    /**
     * 总结文档路径
     */
    @Excel(name = "总结文档路径")
    private String summaryFilePath;
    /**
     * 总结文档名称
     */
    @Excel(name = "总结文档名称")
    private String summaryFileName;
    /**
     * 照片路径数组
     */
    @Excel(name = "照片路径数组")
    private String photoPaths;

    /** 旧文件路径 */
    private List<String> existingProofMaterial;

    /**
     * 学期
     */
    @Excel(name = "学期")
    private String semester;

    /**
     * 提交时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "提交时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date submitTime;

    /**
     * 学生评价
     */
    @Excel(name = "学生评价")
    private String studentComment;

    /**
     * 审核状态
     */
    @Excel(name = "审核状态")
    private String auditStatus;

    /**
     * 审核时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "审核时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date auditTime;

    /**
     * 审核意见
     */
    @Excel(name = "审核意见")
    private String auditRemark;

}
