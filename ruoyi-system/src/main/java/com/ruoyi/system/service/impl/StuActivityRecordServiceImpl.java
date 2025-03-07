package com.ruoyi.system.service.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.StuActivityRecordMapper;
import com.ruoyi.system.domain.StuActivityRecord;
import com.ruoyi.system.service.IStuActivityRecordService;
import org.springframework.transaction.annotation.Transactional;

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

    @Override
    @Transactional
    public int updateActivityAuditInfo(StuActivityRecord activity) {
//        // 校验活动是否存在
//        StuActivityRecord existActivity = stuActivityRecordMapper.selectActivityById(activity.getActivityId());
//        if (existActivity == null) {
//            throw new ServiceException("活动不存在");
//        }

        // 校验审核状态是否合法
        if (!Arrays.asList("已通过", "未通过").contains(activity.getAuditStatus())) {
            throw new ServiceException("无效的审核状态");
        }

        // 构建更新参数（仅更新审核相关字段）
        return stuActivityRecordMapper.updateActivityAuditInfo(activity);
    }

    @Override
    public List<StuActivityRecord> selectAuditActivityRecordList(StuActivityRecord stuActivityRecord) {
        return stuActivityRecordMapper.selectAuditActivityRecordList(stuActivityRecord);
    }

    @Override
    public AjaxResult checkUnique(StuActivityRecord activity) {
        boolean exists = stuActivityRecordMapper.existsByUniqueFields(
                activity.getStudentId(),
                activity.getActivityName(),
                activity.getActivityLevel(),
                activity.getAwardLevel(),
                activity.getSemester()
        );
        return exists ? AjaxResult.error("已存在相同记录") : AjaxResult.success();
    }
}
