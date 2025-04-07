package com.ruoyi.system.service.impl;

import com.ruoyi.system.domain.StuAbilityScore;
import com.ruoyi.system.domain.StudentAbilityScore;
import com.ruoyi.system.domain.dto.GpaResultDTO;
import com.ruoyi.system.mapper.StuAbilityScoreMapper;
import com.ruoyi.system.mapper.StuMajorMapper;
import com.ruoyi.system.mapper.StuScoreMapper;
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
    {
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