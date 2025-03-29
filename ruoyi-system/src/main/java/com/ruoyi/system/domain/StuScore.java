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

    /** 姓名 */
    private String studentName;

    /** 课程代码 */
    @Excel(name = "课程代码")
    private String courseCode;

    /** 课程名称 */
    private String courseName;

    /** 修读学年 */
    @Excel(name = "修读学年")
    private String academicYear;

    /** 修读学期 */
    @Excel(name = "修读学期")
    private String semester;

    /** 学分值 */
    private Long credit;

    /** 课程类型 */
    private String courseCategory;

    /** 课程绩点 */
    private BigDecimal gpa;

    /** 成绩 */
    @Excel(name = "成绩")
    private String scoreValue;

    /** 成绩类型 */
    @Excel(name = "成绩类型")
    private String scoreType;

    /** 录入人姓名 */
    private String nickName;

    /** 数据插入时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date uploadTime;

    private Integer courseRank;

    private Integer totalStudents;

    private Double percentRank;

    public Double getPercentRank() {
        return percentRank;
    }

    public void setPercentRank(Double percentRank) {
        this.percentRank = percentRank;
    }

    public Integer getCourseRank() {
        return courseRank;
    }

    public void setCourseRank(Integer courseRank) {
        this.courseRank = courseRank;
    }

    public Integer getTotalStudents() {
        return totalStudents;
    }

    public void setTotalStudents(Integer totalStudents) {
        this.totalStudents = totalStudents;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public Long getCredit() {
        return credit;
    }

    public void setCredit(Long credit) {
        this.credit = credit;
    }

    public String getCourseCategory() {
        return courseCategory;
    }

    public void setCourseCategory(String courseCategory) {
        this.courseCategory = courseCategory;
    }

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
        if ("通过".equals(String.valueOf(this.scoreValue))||"不通过".equals(String.valueOf(this.scoreValue))) {
            return BigDecimal.valueOf("通过".equals(String.valueOf(this.scoreValue)) ? 1.0 : 0.0);
        }

        try {
            int score = Integer.parseInt(String.valueOf(this.scoreValue));
            if (score < 60) return BigDecimal.valueOf(0.0);
            if (score == 100) return BigDecimal.valueOf(5.0);
            return BigDecimal.valueOf(Math.min(4.9, 1.0 + (score - 60) * 0.1));
        } catch (NumberFormatException e) {
            return BigDecimal.valueOf(0.0);
        }
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
