package com.ruoyi.system.service.impl;

import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.domain.AcademicSummary;
import com.ruoyi.system.domain.StuActivityRecord;
import com.ruoyi.system.domain.StuCompetitionRecord;
import com.ruoyi.system.domain.StuScore;
import com.ruoyi.system.mapper.StuActivityRecordMapper;
import com.ruoyi.system.mapper.StuCompetitionRecordMapper;
import com.ruoyi.system.mapper.StuScoreMapper;
import com.ruoyi.system.service.StudentProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

// StudentProfileServiceImpl.java 增强版
@Service
public class StudentProfileServiceImpl implements StudentProfileService {

    @Autowired
    private StuScoreMapper academicClient;
    @Autowired
    private StuCompetitionRecordMapper competitionService;
    @Autowired
    private StuActivityRecordMapper activityGateway;

    private String getCurrentStudentId() {
        return "20210000000";
    }

    // 增强版学业成绩查询
    @Override
    public AcademicSummary getAcademicRecord(String studentId) {
        StuScore param = new StuScore();
        param.setStudentId(studentId);
        List<StuScore> records = academicClient.selectStuScoreList(param);

        return new AcademicSummary(
                calculateGPA(records),
                calculateTotalCredits(records),
                getCoreCoursePerformance(records)
        );
    }

    @Override
    public List<StuCompetitionRecord> getCompetitions(String studentId) {
        StuCompetitionRecord competition = new StuCompetitionRecord();
        competition.setStudentId(getCurrentStudentId());
        competition.setAuditStatus("已通过");
        return competitionService.selectStuCompetitionRecordList(competition);
    }

    @Override
    public List<StuActivityRecord> getArtSportsActivities(String studentId) {
        StuActivityRecord activity = new StuActivityRecord();
        activity.setStudentId(getCurrentStudentId());
        activity.setAuditStatus("已通过");
        return activityGateway.selectStuActivityRecordList(activity);
    }

    private BigDecimal calculateGPA(List<StuScore> records) {
        return records.stream()
                .map(StuScore::getGpa)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(records.size()), 2, RoundingMode.HALF_UP);
    }
    private Integer calculateTotalCredits(List<StuScore> records) {
        return records.stream()
                .map(record -> record.getCredit() != null ? record.getCredit().intValue() : 0)
                .reduce(0, Integer::sum);
    }

    private String getCoreCoursePerformance(List<StuScore> records) {
        return records.stream()
                .map(record -> {
                    String courseName = record.getCourseName() != null ? record.getCourseName() : "未知课程";
                    String score = record.getScoreValue() != null ? record.getScoreValue() : "无成绩";
                    return String.format("%s(%s)", courseName, score);
                })
                .collect(Collectors.joining(" "));
    }
    // 竞赛记录格式化
    @Override
    public String formatCompetitions(List<StuCompetitionRecord> competitions) {
        return competitions.stream()
                .map(c -> String.format("%s-%s %s（%s）",
                        c.getCompetitionName(),
                        c.getCompetitionLevel(),
                        c.getAwardLevel(),
                        formatSemester(c.getSemester())))
                .collect(Collectors.joining("\n"));
    }

    // 活动记录格式化
    @Override
    public String formatActivities(List<StuActivityRecord> activities) {
        return activities.stream()
                .map(a -> String.format("%s %s（%s学期）",
                        a.getActivityName(),
                        a.getAwardLevel(),
                        formatSemester(a.getSemester())))
                .collect(Collectors.joining("\n"));
    }

    private String formatSemester(String semester) {
        return semester.replaceAll("学年", "-").replace("学期", "");
    }
}