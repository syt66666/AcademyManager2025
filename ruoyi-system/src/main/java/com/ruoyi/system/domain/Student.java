package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 学生管理对象 student
 * 
 * @author ruoyi
 * @date 2025-01-04
 */
public class Student extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /**  */
    @Excel(name = "")
    private Long id;

    /** 学号 */
    @Excel(name = "学号")
    private String studentId;

    /** 姓名 */
    @Excel(name = "姓名")
    private String studentName;

    /** 管理部门 */
    @Excel(name = "管理部门")
    private String academy;

    /** 系统内专业 */
    @Excel(name = "系统内专业")
    private String systemMajor;

    /** 录取专业 */
    @Excel(name = "录取专业")
    private String major;

    /** 行政班 */
    @Excel(name = "行政班")
    private String studentClass;

    /** 分流形式 */
    @Excel(name = "分流形式")
    private String divertForm;

    /** 创新班或拔尖班 */
    @Excel(name = "创新班或拔尖班")
    private Integer innovationClass;

    /** 性别 */
    @Excel(name = "性别")
    private String studentSex;

    /** 转后专业 */
    @Excel(name = "转后专业")
    private String afterMajor;

    /** 转后学院 */
    @Excel(name = "转后学院")
    private String afterAcademy;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setStudentId(String studentId) 
    {
        this.studentId = studentId;
    }

    public String getStudentId() 
    {
        return studentId;
    }
    public void setStudentName(String studentName) 
    {
        this.studentName = studentName;
    }

    public String getStudentName() 
    {
        return studentName;
    }
    public void setAcademy(String academy)
    {
        this.academy = academy;
    }

    public String getAcademy()
    {
        return academy;
    }
    public void setSystemMajor(String systemMajor) 
    {
        this.systemMajor = systemMajor;
    }

    public String getSystemMajor() 
    {
        return systemMajor;
    }
    public void setMajor(String major)
    {
        this.major = major;
    }

    public String getMajor() 
    {
        return major;
    }
    public void setStudentClass(String studentClass) 
    {
        this.studentClass = studentClass;
    }

    public String getStudentClass() 
    {
        return studentClass;
    }
    public void setDivertForm(String divertForm) 
    {
        this.divertForm = divertForm;
    }

    public String getDivertForm() 
    {
        return divertForm;
    }
    public void setInnovationClass(Integer innovationClass)
    {
        this.innovationClass = innovationClass;
    }

    public Integer getInnovationClass() 
    {
        return innovationClass;
    }
    public void setStudentSex(String studentSex) 
    {
        this.studentSex = studentSex;
    }

    public String getStudentSex() 
    {
        return studentSex;
    }
    public void setAfterMajor(String afterMajor) 
    {
        this.afterMajor = afterMajor;
    }

    public String getAfterMajor() 
    {
        return afterMajor;
    }
    public void setAfterAcademy(String afterAcademy) 
    {
        this.afterAcademy = afterAcademy;
    }

    public String getAfterAcademy() 
    {
        return afterAcademy;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("studentId", getStudentId())
            .append("studentName", getStudentName())
            .append("academy", getAcademy())
            .append("systemMajor", getSystemMajor())
            .append("Major", getMajor())
            .append("studentClass", getStudentClass())
            .append("divertForm", getDivertForm())
            .append("innovationClass", getInnovationClass())
            .append("studentSex", getStudentSex())
            .append("afterMajor", getAfterMajor())
            .append("afterAcademy", getAfterAcademy())
            .toString();
    }
}
