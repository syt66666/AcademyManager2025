package com.ruoyi.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 导师学生关联对象 tutor_student_relation
 *
 * @author ruoyi
 * @date 2025-10-24
 */
public class TutorStudentRelation extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 关联ID（主键） */
    private Long relationId;

    /** 教工号 */
    @Excel(name = "教工号")
    private String tutorId;

    /** 学号 */
    @Excel(name = "学号")
    private String studentId;

    /** 年份 */
    @Excel(name = "年份")
    private String year;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date createdAt;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date updatedAt;

    public void setRelationId(Long relationId)
    {
        this.relationId = relationId;
    }

    public Long getRelationId()
    {
        return relationId;
    }
    public void setTutorId(String tutorId)
    {
        this.tutorId = tutorId;
    }

    public String getTutorId()
    {
        return tutorId;
    }
    public void setStudentId(String studentId)
    {
        this.studentId = studentId;
    }

    public String getStudentId()
    {
        return studentId;
    }
    public void setYear(String year)
    {
        this.year = year;
    }

    public String getYear()
    {
        return year;
    }
    public void setCreatedAt(Date createdAt)
    {
        this.createdAt = createdAt;
    }

    public Date getCreatedAt()
    {
        return createdAt;
    }
    public void setUpdatedAt(Date updatedAt)
    {
        this.updatedAt = updatedAt;
    }

    public Date getUpdatedAt()
    {
        return updatedAt;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("relationId", getRelationId())
                .append("tutorId", getTutorId())
                .append("studentId", getStudentId())
                .append("year", getYear())
                .append("createdAt", getCreatedAt())
                .append("updatedAt", getUpdatedAt())
                .toString();
    }
}
