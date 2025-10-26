package com.ruoyi.system.domain;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 课程审核导出DTO对象（仅包含导出字段）
 *
 * @author ruoyi
 * @date 2025-10-26
 */
public class CourseAuditExportDTO extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 学生学号 */
    @Excel(name = "学生学号", cellType = Excel.ColumnType.STRING)
    private String studentId;

    /** 学生姓名 */
    @Excel(name = "学生姓名")
    private String studentName;

    /** 书院信息 */
    @Excel(name = "书院信息")
    private String academy;

    /** 课程名称 */
    @Excel(name = "课程名称")
    private String courseName;

    /** 课程种类 */
    @Excel(name = "课程种类", readConverterExp = "必修=必修课,选修=选修课")
    private String courseCategory;

    /** 课程类型 */
    @Excel(name = "课程类型", readConverterExp = "1=人格塑造与价值引领课程类,2=知识融合与思维进阶课程类,3=能力锻造与实践创新课程类,4=社会责任与领军意识课程类")
    private String courseType;

    /** 课程成绩 */
    @Excel(name = "课程成绩")
    private String scoreValue;

    /** 考核状态 */
    @Excel(name = "考核状态", readConverterExp = "未考核=未考核,未审核=未审核,已通过=已通过,未通过=未通过")
    private String status;

    // Getters and Setters
    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getAcademy() {
        return academy;
    }

    public void setAcademy(String academy) {
        this.academy = academy;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCourseCategory() {
        return courseCategory;
    }

    public void setCourseCategory(String courseCategory) {
        this.courseCategory = courseCategory;
    }

    public String getCourseType() {
        return courseType;
    }

    public void setCourseType(String courseType) {
        this.courseType = courseType;
    }

    public String getScoreValue() {
        return scoreValue;
    }

    public void setScoreValue(String scoreValue) {
        this.scoreValue = scoreValue;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "CourseAuditExportDTO{" +
                "studentId='" + studentId + '\'' +
                ", studentName='" + studentName + '\'' +
                ", academy='" + academy + '\'' +
                ", courseName='" + courseName + '\'' +
                ", courseCategory='" + courseCategory + '\'' +
                ", courseType='" + courseType + '\'' +
                ", scoreValue='" + scoreValue + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}

