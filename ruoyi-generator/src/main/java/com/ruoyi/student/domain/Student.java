package com.ruoyi.student.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 学生管理对象 student
 * 
 * @author ruoyiiiiiii
 * @date 2024-12-25
 */
public class Student extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 序号 */
    private Long id;

    /** 学号 */
    @Excel(name = "学号")
    private String 学号;

    /** 姓名 */
    @Excel(name = "姓名")
    private String 姓名;

    /** 管理部门 */
    @Excel(name = "管理部门")
    private String 管理部门;

    /** 系统内专业 */
    @Excel(name = "系统内专业")
    private String 系统内专业;

    /** 招生录取专业 */
    @Excel(name = "招生录取专业")
    private String 招生录取专业;

    /** 行政班 */
    @Excel(name = "行政班")
    private String 行政班;

    /** 备注 */
    private String 备注;

    /** 分流形式 */
    @Excel(name = "分流形式")
    private String 分流形式;

    /** 国家和高校专项计划学生标志 */
    private Integer 国家和高校专项计划学生标志;

    /** 英文姓名 */
    private String 英文姓名;

    /** 性别 */
    private String 性别;

    /** 密码 */
    private String password;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void set学号(String 学号) 
    {
        this.学号 = 学号;
    }

    public String get学号() 
    {
        return 学号;
    }
    public void set姓名(String 姓名) 
    {
        this.姓名 = 姓名;
    }

    public String get姓名() 
    {
        return 姓名;
    }
    public void set管理部门(String 管理部门) 
    {
        this.管理部门 = 管理部门;
    }

    public String get管理部门() 
    {
        return 管理部门;
    }
    public void set系统内专业(String 系统内专业) 
    {
        this.系统内专业 = 系统内专业;
    }

    public String get系统内专业() 
    {
        return 系统内专业;
    }
    public void set招生录取专业(String 招生录取专业) 
    {
        this.招生录取专业 = 招生录取专业;
    }

    public String get招生录取专业() 
    {
        return 招生录取专业;
    }
    public void set行政班(String 行政班) 
    {
        this.行政班 = 行政班;
    }

    public String get行政班() 
    {
        return 行政班;
    }
    public void set备注(String 备注) 
    {
        this.备注 = 备注;
    }

    public String get备注() 
    {
        return 备注;
    }
    public void set分流形式(String 分流形式) 
    {
        this.分流形式 = 分流形式;
    }

    public String get分流形式() 
    {
        return 分流形式;
    }
    public void set国家和高校专项计划学生标志(Integer 国家和高校专项计划学生标志) 
    {
        this.国家和高校专项计划学生标志 = 国家和高校专项计划学生标志;
    }

    public Integer get国家和高校专项计划学生标志() 
    {
        return 国家和高校专项计划学生标志;
    }
    public void set英文姓名(String 英文姓名) 
    {
        this.英文姓名 = 英文姓名;
    }

    public String get英文姓名() 
    {
        return 英文姓名;
    }
    public void set性别(String 性别) 
    {
        this.性别 = 性别;
    }

    public String get性别() 
    {
        return 性别;
    }
    public void setPassword(String password) 
    {
        this.password = password;
    }

    public String getPassword() 
    {
        return password;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("学号", get学号())
            .append("姓名", get姓名())
            .append("管理部门", get管理部门())
            .append("系统内专业", get系统内专业())
            .append("招生录取专业", get招生录取专业())
            .append("行政班", get行政班())
            .append("备注", get备注())
            .append("分流形式", get分流形式())
            .append("国家和高校专项计划学生标志", get国家和高校专项计划学生标志())
            .append("英文姓名", get英文姓名())
            .append("性别", get性别())
            .append("password", getPassword())
            .toString();
    }
}
