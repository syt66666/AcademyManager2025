package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.StuAbilityScore;
import com.ruoyi.system.domain.StudentAbilityScore;
import com.ruoyi.system.domain.dto.GpaResultDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface StuAbilityScoreMapper {
    List<GpaResultDTO> selectRequiredGPA(@Param("studentIds") List<String> studentIds);
    void batchUpdateAcademicScores(@Param("list") List<StudentAbilityScore> scores);
    public int updateStuAbilityScore(StuAbilityScore stuAbilityScore);
    public StuAbilityScore selectStuAbilityScoreByStudentId(String studentId);
}
