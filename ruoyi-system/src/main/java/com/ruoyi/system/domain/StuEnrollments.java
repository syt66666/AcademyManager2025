package com.ruoyi.system.domain;

import com.ruoyi.common.annotation.Excel;
import lombok.Data;

import java.util.Date;

@Data
public class StuEnrollments {

    private Long enrollmentId;

    @Excel(name = "学生ID")
    private String studentId;
    @Excel(name = "学生姓名")
    private String studentName;
    @Excel(name = "课程ID")
    private String courseCode;
    @Excel(name = "所属书院")
    private String academy;
    private Date enrollmentTime;
}
