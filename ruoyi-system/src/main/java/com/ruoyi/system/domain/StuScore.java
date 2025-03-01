package com.ruoyi.system.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;


public class StuScore extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long scoreId;

    /** 学号 */
    @Excel(name = "学号")
    private String studentId;

    /** 课程编码 */
    @Excel(name = "课程编码")
    private String courseCode;

    /** 修读学年 */
    @Excel(name = "修读学年")
    private String academicYear;

    /** 修读学期 */
    @Excel(name = "修读学期")
    private String semester;

    /** 成绩值 */
    @Excel(name = "成绩值")
    private String scoreValue;

    /** 课程绩点（等第制可为NULL） */
    @Excel(name = "课程绩点", readConverterExp = "等=第制可为NULL")
    private BigDecimal gpa;

    /** 成绩类型：正考，补考，重修，免修 */
    @Excel(name = "成绩类型：正考，补考，重修，免修")
    private String scoreType;

    /** 录入人姓名 */
    @Excel(name = "录入人姓名")
    private String nickName;

    /** 数据插入时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "数据插入时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date uploadTime;

    public void setScoreId(Long scoreId)
    {
        this.scoreId = scoreId;
    }

    public Long getScoreId()
    {
        return scoreId;
    }
    public void setStudentId(String studentId)
    {
        this.studentId = studentId;
    }

    public String getStudentId()
    {
        return studentId;
    }
    public void setCourseCode(String courseCode)
    {
        this.courseCode = courseCode;
    }

    public String getCourseCode()
    {
        return courseCode;
    }
    public void setAcademicYear(String academicYear)
    {
        this.academicYear = academicYear;
    }

    public String getAcademicYear()
    {
        return academicYear;
    }
    public void setSemester(String semester)
    {
        this.semester = semester;
    }

    public String getSemester()
    {
        return semester;
    }
    public void setScoreValue(String scoreValue)
    {
        this.scoreValue = scoreValue;
    }

    public String getScoreValue()
    {
        return scoreValue;
    }
    public void setGpa(BigDecimal gpa)
    {
        this.gpa = gpa;
    }

    public BigDecimal getGpa()
    {
        return gpa;
    }
    public void setScoreType(String scoreType)
    {
        this.scoreType = scoreType;
    }

    public String getScoreType()
    {
        return scoreType;
    }
    public void setNickName(String nickName)
    {
        this.nickName = nickName;
    }

    public String getNickName()
    {
        return nickName;
    }
    public void setUploadTime(Date uploadTime)
    {
        this.uploadTime = uploadTime;
    }

    public Date getUploadTime()
    {
        return uploadTime;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("scoreId", getScoreId())
                .append("studentId", getStudentId())
                .append("courseCode", getCourseCode())
                .append("academicYear", getAcademicYear())
                .append("semester", getSemester())
                .append("scoreValue", getScoreValue())
                .append("gpa", getGpa())
                .append("scoreType", getScoreType())
                .append("nickName", getNickName())
                .append("uploadTime", getUploadTime())
                .toString();
    }
}
