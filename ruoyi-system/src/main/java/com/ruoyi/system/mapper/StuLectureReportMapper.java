package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.StudentLectureReport;
import org.apache.ibatis.annotations.Mapper;

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
    public List<StudentLectureReport> getAllLectureReportRecords(String id);

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
}
