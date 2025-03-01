package com.ruoyi.system.service;


import com.ruoyi.system.domain.StudentLectureReport;
import com.ruoyi.system.domain.vo.StuLectureReportVo;

import java.util.List;

public interface StudentLectureReportService {
    /**
     * 插入学生讲座报告信息
     * @param studentLectureReport
     */
    public void insertStudentLectureReport(StudentLectureReport studentLectureReport);

    /**
     * 查询学生讲座报告信息
     * @return
     */
    List<StuLectureReportVo> getAllCompetitionRecords();
}
