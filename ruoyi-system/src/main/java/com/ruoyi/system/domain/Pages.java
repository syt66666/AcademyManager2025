package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 问卷信息对象 pages
 * 
 * @author ruoyi
 * @date 2024-12-26
 */
public class Pages extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** ID */
    private Long id;

    /** 模板名称 */
    @Excel(name = "模板名称")
    private String name;

    /** 模板介绍 */
    @Excel(name = "模板介绍")
    private String descr;

    /** 封面图 */
    @Excel(name = "封面图")
    private String img;

    /** 使用次数 */
    @Excel(name = "使用次数")
    private Long count;

    /** 创建人ID */
    @Excel(name = "创建人ID")
    private Long userId;

    /** 是否公开 */
    @Excel(name = "是否公开")
    private String open;

    /** 是否发布 */
    @Excel(name = "是否发布")
    private String saved;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setName(String name) 
    {
        this.name = name;
    }

    public String getName() 
    {
        return name;
    }
    public void setDescr(String descr) 
    {
        this.descr = descr;
    }

    public String getDescr() 
    {
        return descr;
    }
    public void setImg(String img) 
    {
        this.img = img;
    }

    public String getImg() 
    {
        return img;
    }
    public void setCount(Long count) 
    {
        this.count = count;
    }

    public Long getCount() 
    {
        return count;
    }
    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    public Long getUserId() 
    {
        return userId;
    }
    public void setOpen(String open) 
    {
        this.open = open;
    }

    public String getOpen() 
    {
        return open;
    }
    public void setSaved(String saved) 
    {
        this.saved = saved;
    }

    public String getSaved() 
    {
        return saved;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("name", getName())
            .append("descr", getDescr())
            .append("img", getImg())
            .append("count", getCount())
            .append("userId", getUserId())
            .append("createTime", getCreateTime())
            .append("open", getOpen())
            .append("saved", getSaved())
            .toString();
    }
}
