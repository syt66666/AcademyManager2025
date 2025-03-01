package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 课程信息对象 stu_course
 *
 * @author ruoyi
 * @date 2025-03-01
 */
public class StuCourse extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 课程唯一ID */
    private Long courseId;

    /** 课程唯一编号（如CS101） */
    @Excel(name = "课程唯一编号", readConverterExp = "如=CS101")
    private String courseCode;

    /** 课程全称 */
    @Excel(name = "课程全称")
    private String courseName;

    /** 学分值 */
    @Excel(name = "学分值")
    private Long credit;

    /** 课程类型 */
    @Excel(name = "课程类型")
    private String courseCategory;

    /** 授课教师 */
    @Excel(name = "授课教师")
    private String teacherName;

    /** 开课院系 */
    @Excel(name = "开课院系")
    private String academy;

    /** 总课时数 */
    @Excel(name = "总课时数")
    private Integer courseHours;

    /** 是否有效课程 */
    @Excel(name = "是否有效课程")
    private Integer isActive;

    public void setCourseId(Long courseId)
    {
        this.courseId = courseId;
    }

    public Long getCourseId()
    {
        return courseId;
    }
    public void setCourseCode(String courseCode)
    {
        this.courseCode = courseCode;
    }

    public String getCourseCode()
    {
        return courseCode;
    }
    public void setCourseName(String courseName)
    {
        this.courseName = courseName;
    }

    public String getCourseName()
    {
        return courseName;
    }
    public void setCredit(Long credit)
    {
        this.credit = credit;
    }

    public Long getCredit()
    {
        return credit;
    }
    public void setCourseCategory(String courseCategory)
    {
        this.courseCategory = courseCategory;
    }

    public String getCourseCategory()
    {
        return courseCategory;
    }
    public void setTeacherName(String teacherName)
    {
        this.teacherName = teacherName;
    }

    public String getTeacherName()
    {
        return teacherName;
    }
    public void setAcademy(String academy)
    {
        this.academy = academy;
    }

    public String getAcademy()
    {
        return academy;
    }
    public void setCourseHours(Integer courseHours)
    {
        this.courseHours = courseHours;
    }

    public Integer getCourseHours()
    {
        return courseHours;
    }
    public void setIsActive(Integer isActive)
    {
        this.isActive = isActive;
    }

    public Integer getIsActive()
    {
        return isActive;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("courseId", getCourseId())
                .append("courseCode", getCourseCode())
                .append("courseName", getCourseName())
                .append("credit", getCredit())
                .append("courseCategory", getCourseCategory())
                .append("teacherName", getTeacherName())
                .append("academy", getAcademy())
                .append("courseHours", getCourseHours())
                .append("isActive", getIsActive())
                .toString();
    }
}
