package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.StuCompetitionRecordMapper;
import com.ruoyi.system.domain.StuCompetitionRecord;
import com.ruoyi.system.service.IStuCompetitionRecordService;

/**
 * 学生科创竞赛记录Service业务层处理
 *
 * @author ruoyi
 * @date 2025-03-07
 */
@Service
public class StuCompetitionRecordServiceImpl implements IStuCompetitionRecordService
{
    @Autowired
    private StuCompetitionRecordMapper stuCompetitionRecordMapper;

    /**
     * 查询学生科创竞赛记录
     *
     * @param competitionId 学生科创竞赛记录主键
     * @return 学生科创竞赛记录
     */
    @Override
    public StuCompetitionRecord selectStuCompetitionRecordByCompetitionId(Integer competitionId)
    {
        return stuCompetitionRecordMapper.selectStuCompetitionRecordByCompetitionId(competitionId);
    }

    /**
     * 查询学生科创竞赛记录列表
     *
     * @param stuCompetitionRecord 学生科创竞赛记录
     * @return 学生科创竞赛记录
     */
    @Override
    public List<StuCompetitionRecord> selectStuCompetitionRecordList(StuCompetitionRecord stuCompetitionRecord)
    {
        return stuCompetitionRecordMapper.selectStuCompetitionRecordList(stuCompetitionRecord);
    }

    /**
     * 新增学生科创竞赛记录
     *
     * @param stuCompetitionRecord 学生科创竞赛记录
     * @return 结果
     */
    @Override
    public int insertStuCompetitionRecord(StuCompetitionRecord stuCompetitionRecord)
    {
        return stuCompetitionRecordMapper.insertStuCompetitionRecord(stuCompetitionRecord);
    }

    /**
     * 修改学生科创竞赛记录
     *
     * @param stuCompetitionRecord 学生科创竞赛记录
     * @return 结果
     */
    @Override
    public int updateStuCompetitionRecord(StuCompetitionRecord stuCompetitionRecord)
    {
        return stuCompetitionRecordMapper.updateStuCompetitionRecord(stuCompetitionRecord);
    }

    /**
     * 批量删除学生科创竞赛记录
     *
     * @param competitionIds 需要删除的学生科创竞赛记录主键
     * @return 结果
     */
    @Override
    public int deleteStuCompetitionRecordByCompetitionIds(Integer[] competitionIds)
    {
        return stuCompetitionRecordMapper.deleteStuCompetitionRecordByCompetitionIds(competitionIds);
    }

    /**
     * 删除学生科创竞赛记录信息
     *
     * @param competitionId 学生科创竞赛记录主键
     * @return 结果
     */
    @Override
    public int deleteStuCompetitionRecordByCompetitionId(Integer competitionId)
    {
        return stuCompetitionRecordMapper.deleteStuCompetitionRecordByCompetitionId(competitionId);
    }
}
