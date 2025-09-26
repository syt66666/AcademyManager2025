package com.ruoyi.system.domain;

import lombok.Data;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.EqualsAndHashCode;
import javax.validation.constraints.NotBlank;

@EqualsAndHashCode(callSuper = true)
@Data
public class StuInfo extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 学生学号 */
    @Excel(name = "学生学号")
    private String studentId;

    /** 学生姓名 */
    @Excel(name = "学生姓名")
    private String studentName;

    /** 入学年份 */
    @Excel(name = "入学年份")
    private String enrollmentYear;

    /** 所属书院 */
    @Excel(name = "所属书院")
    private String academy;

    /** 原系统内专业 */
    @Excel(name = "系统内专业")
    @NotBlank(message = "原系统内专业不能为空")
    private String originalSystemMajor;

    /** 录取专业 */
    @Excel(name = "录取专业")
    private String major;

    /** 分流后系统内专业 */
    @Excel(name = "分流后系统内专业")
    private String systemMajor;

    /** 行政班 */
    @Excel(name = "行政班")
    @NotBlank(message = "行政班不能为空")
    private String studentClass;

    /** 分流形式 */
    @Excel(name = "分流形式")
    @NotBlank(message = "分流形式不能为空")
    private String divertForm;

    /** 是否创新班或拔尖班（0不是 1是） */
    @Excel(name = "是否创新班或拔尖班", readConverterExp = "1=是,0=否")
    private Integer innovationClass;

    /** 创新班政策状态（0保持 1放弃） */
    @Excel(name = "创新班政策状态", readConverterExp = "0=保持,1=放弃")
    private Integer policyStatus;

    /** 学生性别 */
    @Excel(name = "学生性别")
    private String studentSex;

    /** 学生选专业类型 */
    @Excel(name = "学生选专业类型")
    private Integer changeMajorType ;

}
