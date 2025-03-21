package com.ruoyi.system.service.impl;

import java.util.*;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.ServletUtils;
import com.ruoyi.common.utils.ip.IpUtils;
import com.ruoyi.system.domain.AuditHistory;
import com.ruoyi.system.domain.StuActivityRecord;
import com.ruoyi.system.mapper.AuditHistoryMapper;
import com.ruoyi.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.StuActivityRecordMapper;
import com.ruoyi.system.service.IStuActivityRecordService;
import org.springframework.transaction.annotation.Transactional;


@Service
public class StuActivityRecordServiceImpl implements IStuActivityRecordService
{
    @Autowired
    private StuActivityRecordMapper stuActivityRecordMapper;
    @Autowired
    private AuditHistoryMapper auditHistoryMapper;
    @Autowired
    private ISysUserService userService;
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
        // 1. 获取原始状态
        StuActivityRecord originalRecord = stuActivityRecordMapper
                .selectStuActivityRecordByActivityId(activity.getActivityId());
        String beforeStatus = originalRecord.getAuditStatus();

        // 2. 执行审核状态更新
        int updateResult = stuActivityRecordMapper.updateActivityAuditInfo(activity);
        if (updateResult <= 0) {
            throw new ServiceException("审核状态更新失败");
        }

        // 3. 保存审核记录
        saveAuditHistory(
                activity.getActivityId(),
                beforeStatus,
                activity.getAuditStatus(),
                activity.getAuditRemark()
        );

        return updateResult;
    }

    // 新增方法：保存审核历史
    private void saveAuditHistory(Integer activityId, String beforeStatus,
                                  String afterStatus, String remark) {
        AuditHistory history = new AuditHistory();

        // 填充模块信息
        history.setModuleType(2);  // 1代表文体活动模块
        history.setModuleId(activityId.longValue());

        // 状态信息
        history.setAuditStatusBefore(beforeStatus);
        history.setAuditStatusAfter(afterStatus);

        // 操作信息
        history.setAuditAction(getAuditAction(beforeStatus, afterStatus));
        history.setAuditRemark(remark);

        // 用户信息
        SysUser currentUser = userService.selectUserById(SecurityUtils.getUserId());
        history.setAuditorId(currentUser.getUserName());

        // 系统信息
        history.setAuditTime(new Date());
        history.setIpAddress(IpUtils.getIpAddr());
        history.setDeviceInfo(ServletUtils.getRequest().getHeader("User-Agent"));

        // 插入记录
        if (auditHistoryMapper.insertAuditHistory(history) <= 0) {
            throw new ServiceException("审核记录保存失败");
        }
    }

    // 辅助方法：判断审核动作
    private String getAuditAction(String beforeStatus, String afterStatus) {
        if ("已通过".equals(afterStatus)) {
            return "审核通过";
        } else if ("未通过".equals(afterStatus)) {
            return "审核拒绝";
        } else if ("未审核".equals(afterStatus)) {
            return "撤回审核";
        }
        return "状态变更";
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

    @Override
    public Map<String, Integer> countAuditStatus() {
        try {
            return stuActivityRecordMapper.countAuditStatus();
        } catch (Exception e) {
            return new HashMap<String, Integer>(){{
                put("pending", 0);
                put("approved", 0);
                put("rejected", 0);
            }};
        }
    }
}
