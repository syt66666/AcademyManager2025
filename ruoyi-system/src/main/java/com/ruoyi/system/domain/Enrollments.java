package com.ruoyi.system.domain;

import java.util.Date;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

@EqualsAndHashCode(callSuper = true)
@Data
public class Enrollments extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 选课记录的唯一标识符
     */
    private Long enrollmentId;

    /**
     * 学生ID
     */
    @Excel(name = "学生ID")
    private String studentId;

    /**
     * 课程ID
     */
    @Excel(name = "课程ID")
    private String courseCode;

    /**
     * 选课时间（默认为当前时间戳）
     */
    @Excel(name = "选课时间", readConverterExp = "默=认为当前时间戳")
    private Date enrollmentTime;

}
