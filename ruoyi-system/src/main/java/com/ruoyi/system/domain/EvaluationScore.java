package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 问卷得分对象 evaluation_score
 * 
 * @author ruoyi
 * @date 2025-10-21
 */
public class EvaluationScore extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键id */
    private Long id;

    /** 用户名称 */
    @Excel(name = "用户名称")
    private String userName;

    /** 问卷得分 */
    @Excel(name = "问卷得分")
    private Long quesScore;

    /** 教师id */
    @Excel(name = "教师id")
    private Long tutorId;

    /** 问卷类型 */
    @Excel(name = "问卷类型")
    private Integer quesType;

    /** 签名图片 */
    @Excel(name = "签名图片")
    private String signature;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setUserName(String userName) 
    {
        this.userName = userName;
    }

    public String getUserName() 
    {
        return userName;
    }
    public void setQuesScore(Long quesScore) 
    {
        this.quesScore = quesScore;
    }

    public Long getQuesScore() 
    {
        return quesScore;
    }
    public void setTutorId(Long tutorId) 
    {
        this.tutorId = tutorId;
    }

    public Long getTutorId() 
    {
        return tutorId;
    }
    public void setQuesType(Integer quesType) 
    {
        this.quesType = quesType;
    }

    public Integer getQuesType() 
    {
        return quesType;
    }
    public void setSignature(String signature) 
    {
        this.signature = signature;
    }

    public String getSignature() 
    {
        return signature;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("userName", getUserName())
            .append("quesScore", getQuesScore())
            .append("tutorId", getTutorId())
            .append("quesType", getQuesType())
            .append("signature", getSignature())
            .toString();
    }
}
