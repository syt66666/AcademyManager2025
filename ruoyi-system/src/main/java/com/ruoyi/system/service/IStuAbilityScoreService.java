package com.ruoyi.system.service;

import com.ruoyi.system.domain.SemesterStatistics;
import com.ruoyi.system.domain.StuAbilityScore;

import java.util.List;

public interface IStuAbilityScoreService {
    void calculateAndStoreGPAScores();

    StuAbilityScore selectStuAbilityScoreByStudentId(String studentId);

    void calculateAndStoreGPAScores(List<String> studentIds);

    SemesterStatistics selectCountBySemester(String semester, String studentId);
}
