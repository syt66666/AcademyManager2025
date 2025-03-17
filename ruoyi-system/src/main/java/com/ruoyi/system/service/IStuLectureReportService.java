package com.ruoyi.system.service;


import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.system.domain.StuLectureReport;

import java.util.List;

public interface IStuLectureReportService {
    /**
     * 插入学生讲座报告信息
     * @param stuLectureReport
     */
    public void insertStudentLectureReport(StuLectureReport stuLectureReport);

    /**
     * 查询学生讲座报告信息
     * @return
     */
    TableDataInfo getAllCompetitionRecords(Integer semester, String studentId);

    /**
     * 更新学生讲座报告信息
     * @param stuLectureReport
     */
    public void updateStudentLectureReport(StuLectureReport stuLectureReport);

    /**
     * 删除学生讲座报告信息
     * @param
     */
    public int deleteStuLectureReportByReportIds(Integer[] reportIds);
    /**
     * 管理员查询学生参与报告信息列表
     *
     * @param stuLectureReport 学生参与报告信息
     * @return 学生参与报告信息集合
     */
    public List<StuLectureReport> selectStuLecReportList(StuLectureReport stuLectureReport);

    int updateMentorshipAuditInfo(StuLectureReport report);
}
