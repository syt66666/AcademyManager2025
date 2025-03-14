package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.StudentLectureReport;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface StuLectureReportMapper {
    /**
     * 插入学生讲座报告信息
     * @param studentLectureReport
     * @return
     */
    public int insertStuLectureInformation(StudentLectureReport studentLectureReport);

    // 查询所有竞赛记录
    public List<StudentLectureReport> getAllLectureReportRecords(@Param("studentId") String studentId,  @Param("semester") Integer semester);

    /**
     * 更新学生讲座报告信息
     * @param studentLectureReport
     * @return
     */
    public int updateStuLectureInformation(StudentLectureReport studentLectureReport);

    /**
     * 批量删除学生讲座报告记录
     *
     * @param
     * @return 结果
     */
    public int deleteStuLectureReportByReportIds(Integer[] reportIds);

    /**
     * 查询学生参与报告信息列表
     *
     * @param studentLectureReport 学生参与报告信息
     * @return 学生参与报告信息集合
     */
    public List<StudentLectureReport> selectStuLecReportList(StudentLectureReport studentLectureReport);

    @Update({
            "update stu_lec_report",
            "set audit_status = #{auditStatus},",
            "audit_remark = #{auditRemark},",
            "audit_time = #{auditTime}",
            "where report_id = #{reportId}"
    })
    int updateMentorshipAuditInfo(StudentLectureReport report);
}
