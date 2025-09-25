package com.ruoyi.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 课程选课记录对象 course_bookings
 *
 * @author ruoyi
 * @date 2025-09-25
 */
public class CourseBookings extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 选课记录ID */
    private Long bookingId;

    /** 学生ID */
    @Excel(name = "学生ID")
    private String studentId;

    /** 课程ID */
    @Excel(name = "课程ID")
    private Long courseId;

    /** 选课时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "选课时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date bookAt;

    /** 证明材料 */
    @Excel(name = "证明材料")
    private String proof;

    /** 学习总结 */
    @Excel(name = "学习总结")
    private String summary;

    /** 状态：待审核、已通过、未通过 */
    @Excel(name = "状态：待审核、已通过、未通过")
    private String status;

    /** 审核人 */
    @Excel(name = "审核人")
    private String reviewer;

    /** 审核意见 */
    @Excel(name = "审核意见")
    private String reviewerComment;

    /** 审核时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "审核时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date reviewTime;

    /** 组织单位 */
    @Excel(name = "组织单位")
    private String organizer;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date createdTime;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date updatedTime;

    public void setBookingId(Long bookingId)
    {
        this.bookingId = bookingId;
    }

    public Long getBookingId()
    {
        return bookingId;
    }
    public void setStudentId(String studentId)
    {
        this.studentId = studentId;
    }

    public String getStudentId()
    {
        return studentId;
    }
    public void setCourseId(Long courseId)
    {
        this.courseId = courseId;
    }

    public Long getCourseId()
    {
        return courseId;
    }
    public void setBookAt(Date bookAt)
    {
        this.bookAt = bookAt;
    }

    public Date getBookAt()
    {
        return bookAt;
    }
    public void setProof(String proof)
    {
        this.proof = proof;
    }

    public String getProof()
    {
        return proof;
    }
    public void setSummary(String summary)
    {
        this.summary = summary;
    }

    public String getSummary()
    {
        return summary;
    }
    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getStatus()
    {
        return status;
    }
    public void setReviewer(String reviewer)
    {
        this.reviewer = reviewer;
    }

    public String getReviewer()
    {
        return reviewer;
    }
    public void setReviewerComment(String reviewerComment)
    {
        this.reviewerComment = reviewerComment;
    }

    public String getReviewerComment()
    {
        return reviewerComment;
    }
    public void setReviewTime(Date reviewTime)
    {
        this.reviewTime = reviewTime;
    }

    public Date getReviewTime()
    {
        return reviewTime;
    }
    public void setOrganizer(String organizer)
    {
        this.organizer = organizer;
    }

    public String getOrganizer()
    {
        return organizer;
    }
    public void setCreatedTime(Date createdTime)
    {
        this.createdTime = createdTime;
    }

    public Date getCreatedTime()
    {
        return createdTime;
    }
    public void setUpdatedTime(Date updatedTime)
    {
        this.updatedTime = updatedTime;
    }

    public Date getUpdatedTime()
    {
        return updatedTime;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("bookingId", getBookingId())
                .append("studentId", getStudentId())
                .append("courseId", getCourseId())
                .append("bookAt", getBookAt())
                .append("proof", getProof())
                .append("summary", getSummary())
                .append("status", getStatus())
                .append("reviewer", getReviewer())
                .append("reviewerComment", getReviewerComment())
                .append("reviewTime", getReviewTime())
                .append("organizer", getOrganizer())
                .append("createdTime", getCreatedTime())
                .append("updatedTime", getUpdatedTime())
                .toString();
    }
}
