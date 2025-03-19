package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.StuLectureReport;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.Date;
import java.util.List;

@Mapper
public interface StuLectureReportMapper {
    /**
     * 插入学生讲座报告信息
     * @param stuLectureReport
     * @return
     */
    public int insertStuLectureInformation(StuLectureReport stuLectureReport);

    // 查询所有竞赛记录
    public List<StuLectureReport> getAllLectureReportRecords(@Param("studentId") String studentId, @Param("semester") Integer semester);

    /**
     * 更新学生讲座报告信息
     * @param stuLectureReport
     * @return
     */
    public int updateStuLectureInformation(StuLectureReport stuLectureReport);

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
     * @param stuLectureReport 学生参与报告信息
     * @return 学生参与报告信息集合
     */
    public List<StuLectureReport> selectStuLecReportList(StuLectureReport stuLectureReport);

    @Update({
            "update stu_lec_report",
            "set audit_status = #{auditStatus},",
            "audit_remark = #{auditRemark},",
            "audit_time = #{auditTime}",
            "where report_id = #{reportId}"
    })
    int updateMentorshipAuditInfo(StuLectureReport report);

    boolean existsByUniqueFields(@Param("studentId") String studentId,
                                 @Param("reportTitle") String reportTitle,
                                 @Param("reporter") String reporter,
                                 @Param("reportLocation") String reportLocation,
                                 @Param("reportDate") Date reportDate,
                                 @Param("semester") Integer semester,
                                 @Param("auditStatus") Integer auditStatus);
}
