package com.ruoyi.system.service;
import com.ruoyi.system.domain.AcademicSummary;
import com.ruoyi.system.domain.StuActivityRecord;
import com.ruoyi.system.domain.StuCompetitionRecord;
import java.util.List;

public interface StudentProfileService {

    /**
     * 获取学生学业摘要信息
     * @param studentId 学生ID
     * @return 学业摘要对象
     */
    AcademicSummary getAcademicRecord(String studentId);

    /**
     * 获取学生竞赛记录
     * @param studentId 学生ID
     * @return 竞赛记录列表
     */
    List<StuCompetitionRecord> getCompetitions(String studentId);

    /**
     * 获取学生文体活动记录
     * @param studentId 学生ID
     * @return 活动记录列表
     */
    List<StuActivityRecord> getArtSportsActivities(String studentId);

    /**
     * 格式化竞赛记录
     * @param competitions 竞赛记录列表
     * @return 格式化后的字符串
     */
    String formatCompetitions(List<StuCompetitionRecord> competitions);

    /**
     * 格式化文体活动记录
     * @param activities 活动记录列表
     * @return 格式化后的字符串
     */
    String formatActivities(List<StuActivityRecord> activities);
}