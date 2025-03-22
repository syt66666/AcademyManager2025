package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;


public class Tutors extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 导师工号（主键） */
    private String tutorId;

    /** 姓名 */
    @Excel(name = "姓名")
    private String tutorName;

    /** 性别 */
    @Excel(name = "性别")
    private String tutorGender;

    /** 职称 */
    @Excel(name = "职称")
    private String tutorTitle;

    /** 所属书院 */
    @Excel(name = "所属书院")
    private String tutorDepartment;

    public void setTutorId(String tutorId)
    {
        this.tutorId = tutorId;
    }

    public String getTutorId()
    {
        return tutorId;
    }
    public void setTutorName(String tutorName)
    {
        this.tutorName = tutorName;
    }

    public String getTutorName()
    {
        return tutorName;
    }
    public void setTutorGender(String tutorGender)
    {
        this.tutorGender = tutorGender;
    }

    public String getTutorGender()
    {
        return tutorGender;
    }
    public void setTutorTitle(String tutorTitle)
    {
        this.tutorTitle = tutorTitle;
    }

    public String getTutorTitle()
    {
        return tutorTitle;
    }
    public void setTutorDepartment(String tutorDepartment)
    {
        this.tutorDepartment = tutorDepartment;
    }

    public String getTutorDepartment()
    {
        return tutorDepartment;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("tutorId", getTutorId())
                .append("tutorName", getTutorName())
                .append("tutorGender", getTutorGender())
                .append("tutorTitle", getTutorTitle())
                .append("tutorDepartment", getTutorDepartment())
                .toString();
    }
}
