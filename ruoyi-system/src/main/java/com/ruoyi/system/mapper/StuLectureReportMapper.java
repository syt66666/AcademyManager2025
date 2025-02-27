package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.StudentLectureReport;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface StuLectureReportMapper {
    /**
     * 插入学生讲座报告信息
     * @param studentLectureReport
     * @return
     */
    public int insertStuLectureInformation(StudentLectureReport studentLectureReport);
}
