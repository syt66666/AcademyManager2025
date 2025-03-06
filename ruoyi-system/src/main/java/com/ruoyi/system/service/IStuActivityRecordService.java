package com.ruoyi.system.service;

import java.util.List;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.StuActivityRecord;

/**
 * 学生文体活动记录Service接口
 *
 * @author ruoyi
 * @date 2025-03-02
 */
public interface IStuActivityRecordService
{
    /**
     * 查询学生文体活动记录
     *
     * @param activityId 学生文体活动记录主键
     * @return 学生文体活动记录
     */
    public StuActivityRecord selectStuActivityRecordByActivityId(Integer activityId);

    /**
     * 查询学生文体活动记录列表
     *
     * @param stuActivityRecord 学生文体活动记录
     * @return 学生文体活动记录集合
     */
    public List<StuActivityRecord> selectStuActivityRecordList(StuActivityRecord stuActivityRecord);

    /**
     * 新增学生文体活动记录
     *
     * @param stuActivityRecord 学生文体活动记录
     * @return 结果
     */
    public int insertStuActivityRecord(StuActivityRecord stuActivityRecord);

    /**
     * 修改学生文体活动记录
     *
     * @param stuActivityRecord 学生文体活动记录
     * @return 结果
     */
    public int updateStuActivityRecord(StuActivityRecord stuActivityRecord);

    /**
     * 批量删除学生文体活动记录
     *
     * @param activityIds 需要删除的学生文体活动记录主键集合
     * @return 结果
     */
    public int deleteStuActivityRecordByActivityIds(Integer[] activityIds);

    /**
     * 删除学生文体活动记录信息
     *
     * @param activityId 学生文体活动记录主键
     * @return 结果
     */
    public int deleteStuActivityRecordByActivityId(Integer activityId);

    public int updateActivityAuditInfo(StuActivityRecord activity);

    public List<StuActivityRecord> selectAuditActivityRecordList(StuActivityRecord stuActivityRecord);

    public AjaxResult checkUnique(StuActivityRecord activity);
}
