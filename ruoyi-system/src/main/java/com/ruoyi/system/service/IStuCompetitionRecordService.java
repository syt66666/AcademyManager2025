package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.StuCompetitionRecord;

/**
 * 学生科创竞赛记录Service接口
 *
 * @author ruoyi
 * @date 2025-03-07
 */
public interface IStuCompetitionRecordService
{
    /**
     * 查询学生科创竞赛记录
     *
     * @param competitionId 学生科创竞赛记录主键
     * @return 学生科创竞赛记录
     */
    public StuCompetitionRecord selectStuCompetitionRecordByCompetitionId(Integer competitionId);

    /**
     * 查询学生科创竞赛记录列表
     *
     * @param stuCompetitionRecord 学生科创竞赛记录
     * @return 学生科创竞赛记录集合
     */
    public List<StuCompetitionRecord> selectStuCompetitionRecordList(StuCompetitionRecord stuCompetitionRecord);

    /**
     * 新增学生科创竞赛记录
     *
     * @param stuCompetitionRecord 学生科创竞赛记录
     * @return 结果
     */
    public int insertStuCompetitionRecord(StuCompetitionRecord stuCompetitionRecord);

    /**
     * 修改学生科创竞赛记录
     *
     * @param stuCompetitionRecord 学生科创竞赛记录
     * @return 结果
     */
    public int updateStuCompetitionRecord(StuCompetitionRecord stuCompetitionRecord);

    /**
     * 批量删除学生科创竞赛记录
     *
     * @param competitionIds 需要删除的学生科创竞赛记录主键集合
     * @return 结果
     */
    public int deleteStuCompetitionRecordByCompetitionIds(Integer[] competitionIds);

    /**
     * 删除学生科创竞赛记录信息
     *
     * @param competitionId 学生科创竞赛记录主键
     * @return 结果
     */
    public int deleteStuCompetitionRecordByCompetitionId(Integer competitionId);
}
