package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.StuMajor;
import com.ruoyi.system.domain.dto.MajorScoreDTO;
import com.ruoyi.system.domain.dto.MajorStatistic;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface StuMajorMapper {
    List<StuMajor> selectByAcademyAndType(
            @Param("major") String major,
            @Param("academy") String academy,
            @Param("type") Integer type
    );
    List<StuMajor> selectChildrenByParentId(Long parentId);
    List<MajorStatistic> selectMajorStatistics(
            @Param("parentId") Integer parentId
    );
    void updateRankedStudents(@Param("majorId") List<Integer> majorIds);
    /**
     * 根据学生ID列表获取所属专业ID（去重）
     * @param studentIds 学生ID列表
     * @return 专业ID列表
     */
    List<Integer> selectDistinctMajorIdsByStudents(
            @Param("studentIds") List<String> studentIds
    );
    public int updateStuMajor(@Param("majorId") Integer majorId,
            @Param("studentNum") Integer studentNum);
}