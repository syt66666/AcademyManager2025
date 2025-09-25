package com.ruoyi.system.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 【请填写功能名称】对象 courses
 *
 * @author ruoyi
 * @date 2025-09-25
 */
public class Courses extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 课程id */
    private Long courseId;

    /** 课程名称 */
    @Excel(name = "课程名称")
    private String courseName;

    /** 课程类型 */
    @Excel(name = "课程类型")
    private Long courseType;

    /** 课程地点 */
    @Excel(name = "课程地点")
    private String courseLocation;

    /** 选课开始时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "选课开始时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date courseStart;

    /** 选课截止时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "选课截止时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date courseDeadline;

    /** 课程开始时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "课程开始时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date startTime;

    /** 课程结束时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "课程结束时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date endTime;

    /** 课程容量 */
    @Excel(name = "课程容量")
    private Long courseTotalCapacity;

    /** 课程剩余容量 */
    @Excel(name = "课程剩余容量")
    private Long courseCapacity;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String courseDescription;

    /** 课程图片URL */
    @Excel(name = "课程图片URL")
    private String pictureUrl;

    /** 课程状态 */
    @Excel(name = "课程状态")
    private String status;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date createdAt;

    /** 组织者 */
    @Excel(name = "组织者")
    private String organizer;

    /** 注意事项 */
    @Excel(name = "注意事项")
    private String notes;

    /** 乐观锁 */
    @Excel(name = "乐观锁")
    private Long version;

    /** 课程学分 */
    @Excel(name = "课程学分")
    private BigDecimal courseCredit;

    /** 课程类别 */
    @Excel(name = "课程类别")
    private String courseCategory;

    public void setCourseId(Long courseId)
    {
        this.courseId = courseId;
    }

    public Long getCourseId()
    {
        return courseId;
    }
    public void setCourseName(String courseName)
    {
        this.courseName = courseName;
    }

    public String getCourseName()
    {
        return courseName;
    }
    public void setCourseType(Long courseType)
    {
        this.courseType = courseType;
    }

    public Long getCourseType()
    {
        return courseType;
    }
    public void setCourseLocation(String courseLocation)
    {
        this.courseLocation = courseLocation;
    }

    public String getCourseLocation()
    {
        return courseLocation;
    }
    public void setCourseStart(Date courseStart)
    {
        this.courseStart = courseStart;
    }

    public Date getCourseStart()
    {
        return courseStart;
    }
    public void setCourseDeadline(Date courseDeadline)
    {
        this.courseDeadline = courseDeadline;
    }

    public Date getCourseDeadline()
    {
        return courseDeadline;
    }
    public void setStartTime(Date startTime)
    {
        this.startTime = startTime;
    }

    public Date getStartTime()
    {
        return startTime;
    }
    public void setEndTime(Date endTime)
    {
        this.endTime = endTime;
    }

    public Date getEndTime()
    {
        return endTime;
    }
    public void setCourseTotalCapacity(Long courseTotalCapacity)
    {
        this.courseTotalCapacity = courseTotalCapacity;
    }

    public Long getCourseTotalCapacity()
    {
        return courseTotalCapacity;
    }
    public void setCourseCapacity(Long courseCapacity)
    {
        this.courseCapacity = courseCapacity;
    }

    public Long getCourseCapacity()
    {
        return courseCapacity;
    }
    public void setCourseDescription(String courseDescription)
    {
        this.courseDescription = courseDescription;
    }

    public String getCourseDescription()
    {
        return courseDescription;
    }
    public void setPictureUrl(String pictureUrl)
    {
        this.pictureUrl = pictureUrl;
    }

    public String getPictureUrl()
    {
        return pictureUrl;
    }
    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getStatus()
    {
        return status;
    }
    public void setCreatedAt(Date createdAt)
    {
        this.createdAt = createdAt;
    }

    public Date getCreatedAt()
    {
        return createdAt;
    }
    public void setOrganizer(String organizer)
    {
        this.organizer = organizer;
    }

    public String getOrganizer()
    {
        return organizer;
    }
    public void setNotes(String notes)
    {
        this.notes = notes;
    }

    public String getNotes()
    {
        return notes;
    }
    public void setVersion(Long version)
    {
        this.version = version;
    }

    public Long getVersion()
    {
        return version;
    }
    public void setCourseCredit(BigDecimal courseCredit)
    {
        this.courseCredit = courseCredit;
    }

    public BigDecimal getCourseCredit()
    {
        return courseCredit;
    }
    public void setCourseCategory(String courseCategory)
    {
        this.courseCategory = courseCategory;
    }

    public String getCourseCategory()
    {
        return courseCategory;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("courseId", getCourseId())
                .append("courseName", getCourseName())
                .append("courseType", getCourseType())
                .append("courseLocation", getCourseLocation())
                .append("courseStart", getCourseStart())
                .append("courseDeadline", getCourseDeadline())
                .append("startTime", getStartTime())
                .append("endTime", getEndTime())
                .append("courseTotalCapacity", getCourseTotalCapacity())
                .append("courseCapacity", getCourseCapacity())
                .append("courseDescription", getCourseDescription())
                .append("pictureUrl", getPictureUrl())
                .append("status", getStatus())
                .append("createdAt", getCreatedAt())
                .append("organizer", getOrganizer())
                .append("notes", getNotes())
                .append("version", getVersion())
                .append("courseCredit", getCourseCredit())
                .append("courseCategory", getCourseCategory())
                .toString();
    }
}
