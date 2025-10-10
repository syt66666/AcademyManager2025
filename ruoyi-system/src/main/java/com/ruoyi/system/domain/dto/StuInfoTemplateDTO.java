package com.ruoyi.system.domain.dto;

import com.ruoyi.common.annotation.Excel;
import lombok.Data;

/**
 * 学生信息导入模板DTO
 * 包含详细的填写要求和数据验证
 *
 * @author ruoyi
 */
@Data
public class StuInfoTemplateDTO {

    /** 学生学号 - 必填，格式：8位数字 */
    @Excel(name = "学生学号", prompt = "必填，格式：11位数字，如：20250000001")
    private String studentId;

    /** 学生姓名 - 必填，2-10个字符 */
    @Excel(name = "学生姓名", prompt = "必填，2-10个字符，支持中文、英文、数字及特殊字符（如·、-等），如：张三、迪雅尔·毛吾兰")
    private String studentName;

    /** 所属书院 - 必填，从下拉列表选择 */
    @Excel(name = "所属书院", prompt = "必填：大煜书院、伯川书院、笃学书院、令希书院、厚德书院、知行书院、求实书院")
    private String academy;

    /** 原系统内专业 - 必填 */
    @Excel(name = "系统内专业", prompt = "必填，如：物质创造学域、智能制造学域、理科强基学域、智能建造学域、人文社科学域、信息技术学域（一）、信息技术学域（二）")
    private String originalSystemMajor;

    /** 行政班 - 必填 */
    @Excel(name = "行政班", prompt = "必填，格式：书院名+年级+班级，如：求实2501班、大煜2502班")
    private String studentClass;

    /** 分流形式 - 必填，从下拉列表选择 */
    @Excel(name = "分流形式", prompt = "必填，请选择：不可变更专业、仅可转专业、可类内任选，并转专业、可类内任选，不可转专业、可域内任选，并转专业")
    private String divertForm;

    /** 是否创新班或拔尖班 - 必填，0或1 */
    @Excel(name = "是否创新班或拔尖班", prompt = "必填，请输入：是/否")
    private String innovationClass;
}
