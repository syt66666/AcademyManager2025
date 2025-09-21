package com.ruoyi.system.domain.dto;

import com.ruoyi.common.annotation.Excel;
import lombok.Data;

/**
 * 学生信息导入模板DTO
 * 只包含必填字段
 *
 * @author ruoyi
 */
@Data
public class StuInfoTemplateDTO {

    /** 学生学号 */
    @Excel(name = "学生学号")
    private String studentId;

    /** 学生姓名 */
    @Excel(name = "学生姓名")
    private String studentName;

    /** 所属书院 */
    @Excel(name = "所属书院")
    private String academy;

    /** 原系统内专业 */
    @Excel(name = "原系统内专业")
    private String originalSystemMajor;

    /** 行政班 */
    @Excel(name = "行政班")
    private String studentClass;

    /** 分流形式 */
    @Excel(name = "分流形式")
    private String divertForm;

    /** 是否创新班或拔尖班 */
    @Excel(name = "是否创新班或拔尖班")
    private String innovationClass;
}
