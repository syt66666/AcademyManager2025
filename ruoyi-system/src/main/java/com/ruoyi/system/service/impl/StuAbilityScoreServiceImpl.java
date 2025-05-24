package com.ruoyi.system.service.impl;

import com.ruoyi.system.domain.*;
import com.ruoyi.system.domain.dto.GpaResultDTO;
import com.ruoyi.system.mapper.*;
import com.ruoyi.system.service.IStuAbilityScoreService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class StuAbilityScoreServiceImpl implements IStuAbilityScoreService {

    @Autowired
    private StuScoreMapper scoreMapper;
    @Autowired
    private StuAbilityScoreMapper abilityMapper;
    @Autowired
    private StuMajorMapper majorMapper;
    @Autowired
    private StuCompetitionRecordMapper competitionMapper;
    @Autowired
    private StuActivityRecordMapper activityMapper;
    @Autowired
    private StuMentorshipRecordMapper mentorshipMapper;
    @Autowired
    private StuLectureReportMapper lectureMapper;

    @Value("${gpa.maxValue:5.0}")
    private BigDecimal maxGpa; // 系统配置最高GPA

    // 原有全量计算方法
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void calculateAndStoreGPAScores() {
        calculateAndStoreGPAScores(Collections.emptyList());
    }
    @Override
    public StuAbilityScore selectStuAbilityScoreByStudentId(String studentId)
    {   StuAbilityScore s = abilityMapper.selectStuAbilityScoreByStudentId(studentId);
        System.out.println(s);
        return abilityMapper.selectStuAbilityScoreByStudentId(studentId);
    }
    // 新增增量计算方法（包可见性，供其他Service调用）
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void calculateAndStoreGPAScores(List<String> studentIds) {
        // 1.获取原始GPA数据（支持全量和增量）
        List<GpaResultDTO> gpaResults = abilityMapper.selectRequiredGPA(studentIds);

        // 2.转换为雷达图分数
        List<StudentAbilityScore> scores = gpaResults.stream()
                .map(this::convertToAbilityScore)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        // 3.分批存储（优化大数据量处理）
        int batchSize = 500;
        List<List<StudentAbilityScore>> partitions = partitionList(scores, batchSize);

        partitions.forEach(batch -> {
            abilityMapper.batchUpdateAcademicScores(batch);
            log.debug("已批量更新{}条学业成绩", batch.size());
        });

        log.info("共更新{}条学业成绩数据，涉及学生{}人",
                scores.size(),
                gpaResults.stream().map(GpaResultDTO::getStudentId).distinct().count());
        List<String> academy = majorMapper.selectDistinctMajorIdsByStudents(studentIds);
        System.out.println("academy:"+academy);
        majorMapper.updateRankedStudents(academy);
    }

    @Override
    public SemesterStatistics selectCountBySemester(String semester, String studentId) {
        // 清理参数（去除双引号和空格）
        semester = semester.replace("\"", "").trim();
        studentId = studentId.replace("\"", "").trim();

        // 查询前三个统计值（使用原始 semester 值）
        Integer competitionCount = competitionMapper.getCountBySemester(semester, studentId);
        Integer activityCount = activityMapper.getCountBySemester(semester, studentId);
        Integer mentorshipCount = mentorshipMapper.getCountBySemester(semester, studentId);

        // 转换 semester 为数字格式（用于 lectureMapper）
        String convertedSemester = convertSemesterToNumber(semester);
        Integer lectureCount = lectureMapper.getCountBySemester(convertedSemester, studentId);

        // 封装结果
        SemesterStatistics stats = new SemesterStatistics();
        stats.setCompetitionCount(competitionCount);
        stats.setActivityCount(activityCount);
        stats.setMentorshipCount(mentorshipCount);
        stats.setLectureCount(lectureCount);

        return stats;
    }

    // 学期名称转数字的辅助方法
    private String convertSemesterToNumber(String semester) {
        switch (semester) {
            case "大一上": return "1";
            case "大一下": return "2";
            case "大二上": return "3";
            case "大二下": return "4";
            case "大三上": return "5";
            case "大三下": return "6";
            case "大四上": return "7";
            case "大四下": return "8";
            default: return semester; // 默认返回原值（或抛异常）
        }
    }

    // 手动实现列表分片（兼容JDK8）
    private <T> List<List<T>> partitionList(List<T> list, int batchSize) {
        List<List<T>> partitions = new ArrayList<>();
        int total = list.size();

        for (int i = 0; i < total; i += batchSize) {
            int end = Math.min(i + batchSize, total);
            partitions.add(list.subList(i, end));
        }
        return partitions;
    }

    // 原有转换方法（添加空值防御）
    private StudentAbilityScore convertToAbilityScore(GpaResultDTO dto) {
        if (dto == null || dto.getRawGpa() == null) {
            log.warn("遇到空GPA数据，学生ID：{}", dto != null ? dto.getStudentId() : "null");
            return null;
        }

        try {
            BigDecimal normalized = dto.getRawGpa()
                    .divide(maxGpa, 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"))
                    .setScale(2, RoundingMode.HALF_UP);

            return new StudentAbilityScore(
                    dto.getStudentId(),
                    normalized, // academic_score
                    null,       // 其他维度留空待更新
                    null,
                    null,
                    null
            );
        } catch (Exception e) {
            log.error("学生{} GPA转换异常: {}", dto.getStudentId(), e.getMessage());
            return null;
        }
    }
}