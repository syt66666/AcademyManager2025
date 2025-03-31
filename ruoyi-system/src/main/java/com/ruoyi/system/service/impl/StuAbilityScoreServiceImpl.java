package com.ruoyi.system.service.impl;

import com.ruoyi.system.domain.StudentAbilityScore;
import com.ruoyi.system.domain.dto.GpaResultDTO;
import com.ruoyi.system.mapper.StuScoreMapper;
import com.ruoyi.system.service.IStuAbilityScoreService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class StuAbilityScoreServiceImpl implements IStuAbilityScoreService {

    @Autowired
    private StuScoreMapper scoreMapper;

    @Value("${gpa.maxValue:5.0}")
    private BigDecimal maxGpa; // 系统配置最高GPA

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void calculateAndStoreGPAScores() {
        // 1.获取原始GPA数据
        List<GpaResultDTO> gpaResults = scoreMapper.selectRequiredGPA();
        System.out.println(gpaResults);
        // 2.转换为雷达图分数
        List<StudentAbilityScore> scores = gpaResults.stream()
                .map(this::convertToAbilityScore)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        // 3.批量存储
        if (!scores.isEmpty()) {
            scoreMapper.batchUpdateAcademicScores(scores);
            log.info("已更新{}条学业成绩数据", scores.size());
        }
    }

    private StudentAbilityScore convertToAbilityScore(GpaResultDTO dto) {
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
