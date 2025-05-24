package com.ruoyi.system.domain;

import com.ruoyi.common.annotation.Excel;
import lombok.Data;

import java.util.Date;

@Data
public class StuEnrollments {

    private Long enrollmentId;

    @Excel(name = "学生ID")
    private String studentId;

    @Excel(name = "课程ID")
    private String courseCode;

    private String academy;
    private String studentName;
    @Excel(name = "选课时间", readConverterExp = "默=认为当前时间戳")
    private Date enrollmentTime;
}
