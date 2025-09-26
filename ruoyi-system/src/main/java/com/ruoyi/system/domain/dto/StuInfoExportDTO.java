package com.ruoyi.system.domain.dto;

import com.ruoyi.common.annotation.Excel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 学生信息导出DTO
 * 只包含指定的导出字段
 * 
 * @author ruoyi
 * @date 2025-01-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class StuInfoExportDTO
{
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

    /** 系统内专业 */
    @Excel(name = "系统内专业")
    private String originalSystemMajor;

    /** 录取专业 */
    @Excel(name = "录取专业")
    private String major;

    /** 行政班 */
    @Excel(name = "行政班")
    private String studentClass;

    /** 分流形式 */
    @Excel(name = "分流形式")
    private String divertForm;

    /** 是否创新班或拔尖班 */
    @Excel(name = "是否创新班或拔尖班", readConverterExp = "1=是,0=否")
    private Integer innovationClass;
}
