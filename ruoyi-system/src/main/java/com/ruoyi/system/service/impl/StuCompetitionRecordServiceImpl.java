package com.ruoyi.system.service.impl;

import java.util.Arrays;
import java.util.List;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.exception.ServiceException;
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

    @Override
    public int updateCompetitionAuditInfo(StuCompetitionRecord competition) {
        // 校验审核状态是否合法
        if (!Arrays.asList("已通过", "未通过").contains(competition.getAuditStatus())) {
            throw new ServiceException("无效的审核状态");
        }

        // 构建更新参数（仅更新审核相关字段）
        return stuCompetitionRecordMapper.updateCompetitionAuditInfo(competition);
    }

    @Override
    public List<StuCompetitionRecord> selectCompetitionRecordList(StuCompetitionRecord stuCompetitionRecord) {
        return stuCompetitionRecordMapper.selectAuditCompetitionRecordList(stuCompetitionRecord);
    }

    @Override
    public AjaxResult checkUnique(StuCompetitionRecord competition) {
        // 打印参数日志（确保无隐藏字符）
        System.out.println(competition.getStudentId()+competition.getCompetitionName()+competition.getCompetitionLevel()+competition.getAwardLevel()+competition.getSemester()
        );
        boolean exists = stuCompetitionRecordMapper.existsByUniqueFields(
                competition.getStudentId(),
                competition.getCompetitionName(),
                competition.getCompetitionLevel(),
                competition.getAwardLevel(),
                competition.getSemester()
        );
        return exists ? AjaxResult.error("已存在相同记录") : AjaxResult.success();
    }
}
