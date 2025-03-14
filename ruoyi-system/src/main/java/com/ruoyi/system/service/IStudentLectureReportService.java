package com.ruoyi.system.service;


import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.system.domain.StudentLectureReport;

import java.util.List;

public interface IStudentLectureReportService {
    /**
     * 插入学生讲座报告信息
     * @param studentLectureReport
     */
    public void insertStudentLectureReport(StudentLectureReport studentLectureReport);

    /**
     * 查询学生讲座报告信息
     * @return
     */
    TableDataInfo getAllCompetitionRecords(Integer semester, String studentId);

    /**
     * 更新学生讲座报告信息
     * @param studentLectureReport
     */
    public void updateStudentLectureReport(StudentLectureReport studentLectureReport);

    /**
     * 删除学生讲座报告信息
     * @param
     */
    public int deleteStuLectureReportByReportIds(Integer[] reportIds);
    /**
     * 管理员查询学生参与报告信息列表
     *
     * @param studentLectureReport 学生参与报告信息
     * @return 学生参与报告信息集合
     */
    public List<StudentLectureReport> selectStuLecReportList(StudentLectureReport studentLectureReport);

    int updateMentorshipAuditInfo(StudentLectureReport report);
}
