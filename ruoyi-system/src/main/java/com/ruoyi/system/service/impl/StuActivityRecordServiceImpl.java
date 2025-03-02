package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.StuActivityRecordMapper;
import com.ruoyi.system.domain.StuActivityRecord;
import com.ruoyi.system.service.IStuActivityRecordService;

/**
 * 学生文体活动记录Service业务层处理
 *
 * @author ruoyi
 * @date 2025-03-02
 */
@Service
public class StuActivityRecordServiceImpl implements IStuActivityRecordService
{
    @Autowired
    private StuActivityRecordMapper stuActivityRecordMapper;

    /**
     * 查询学生文体活动记录
     *
     * @param activityId 学生文体活动记录主键
     * @return 学生文体活动记录
     */
    @Override
    public StuActivityRecord selectStuActivityRecordByActivityId(Integer activityId)
    {
        return stuActivityRecordMapper.selectStuActivityRecordByActivityId(activityId);
    }

    /**
     * 查询学生文体活动记录列表
     *
     * @param stuActivityRecord 学生文体活动记录
     * @return 学生文体活动记录
     */
    @Override
    public List<StuActivityRecord> selectStuActivityRecordList(StuActivityRecord stuActivityRecord)
    {
        return stuActivityRecordMapper.selectStuActivityRecordList(stuActivityRecord);
    }

    /**
     * 新增学生文体活动记录
     *
     * @param stuActivityRecord 学生文体活动记录
     * @return 结果
     */
    @Override
    public int insertStuActivityRecord(StuActivityRecord stuActivityRecord)
    {
        return stuActivityRecordMapper.insertStuActivityRecord(stuActivityRecord);
    }

    /**
     * 修改学生文体活动记录
     *
     * @param stuActivityRecord 学生文体活动记录
     * @return 结果
     */
    @Override
    public int updateStuActivityRecord(StuActivityRecord stuActivityRecord)
    {
        return stuActivityRecordMapper.updateStuActivityRecord(stuActivityRecord);
    }

    /**
     * 批量删除学生文体活动记录
     *
     * @param activityIds 需要删除的学生文体活动记录主键
     * @return 结果
     */
    @Override
    public int deleteStuActivityRecordByActivityIds(Integer[] activityIds)
    {
        return stuActivityRecordMapper.deleteStuActivityRecordByActivityIds(activityIds);
    }

    /**
     * 删除学生文体活动记录信息
     *
     * @param activityId 学生文体活动记录主键
     * @return 结果
     */
    @Override
    public int deleteStuActivityRecordByActivityId(Integer activityId)
    {
        return stuActivityRecordMapper.deleteStuActivityRecordByActivityId(activityId);
    }
}
