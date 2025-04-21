package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;


public class StuCourse extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 课程唯一ID */
    private Long courseId;

    /** 课程唯一编号（如CS101） */
    @Excel(name = "课程代码", readConverterExp = "如=CS101")
    private String courseCode;

    /** 课程全称 */
    @Excel(name = "课程名称")
    private String courseName;

    /** 学分值 */
    @Excel(name = "学分")
    private Long credit;

    /** 课程类型 */
    @Excel(name = "课程类型", readConverterExp = "如必修/选修/通识/特色课")
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

    /** 课程容量 */
    @Excel(name = "课程容量")
    private Long courseCapacity;

    /** 当前选择人数 */
    @Excel(name = "当前选择人数")
    private Long enrolledStudent;

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
    public void setCourseCapacity(Long courseCapacity)
    {
        this.courseCapacity = courseCapacity;
    }

    public Long getCourseCapacity()
    {
        return courseCapacity;
    }
    public void setEnrolledStudent(Long enrolledStudent)
    {
        this.enrolledStudent = enrolledStudent;
    }

    public Long getEnrolledStudent()
    {
        return enrolledStudent;
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
                .append("courseCapacity", getCourseCapacity())
                .append("enrolledStudent", getEnrolledStudent())
                .toString();
    }
}
