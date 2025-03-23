package com.ruoyi.system.service.impl;

import java.util.*;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.ServletUtils;
import com.ruoyi.common.utils.ip.IpUtils;
import com.ruoyi.system.domain.AuditHistory;
import com.ruoyi.system.domain.StuMentorshipRecord;
import com.ruoyi.system.mapper.AuditHistoryMapper;
import com.ruoyi.system.service.ISysUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.StuMentorshipRecordMapper;
import com.ruoyi.system.domain.StuMentorshipRecord;
import com.ruoyi.system.service.IStuMentorshipRecordService;


@Service
@Slf4j
public class StuMentorshipRecordServiceImpl implements IStuMentorshipRecordService
{
    @Autowired
    private StuMentorshipRecordMapper stuMentorshipRecordMapper;
    @Autowired
    private AuditHistoryMapper auditHistoryMapper;
    @Autowired
    private ISysUserService userService;

    /**
     * 查询导师指导记录
     *
     * @param recordId 导师指导记录主键
     * @return 导师指导记录
     */
    @Override
    public StuMentorshipRecord selectStuMentorshipRecordByRecordId(Integer recordId)
    {
        return stuMentorshipRecordMapper.selectStuMentorshipRecordByRecordId(recordId);
    }

    /**
     * 查询导师指导记录列表
     *
     * @param stuMentorshipRecord 导师指导记录
     * @return 导师指导记录
     */
    @Override
    public List<StuMentorshipRecord> selectStuMentorshipRecordList(StuMentorshipRecord stuMentorshipRecord)
    {
        return stuMentorshipRecordMapper.selectStuMentorshipRecordList(stuMentorshipRecord);
    }

    /**
     * 新增导师指导记录
     *
     * @param stuMentorshipRecord 导师指导记录
     * @return 结果
     */
    @Override
    public int insertStuMentorshipRecord(StuMentorshipRecord stuMentorshipRecord)
    {
        return stuMentorshipRecordMapper.insertStuMentorshipRecord(stuMentorshipRecord);
    }

    /**
     * 修改导师指导记录
     *
     * @param stuMentorshipRecord 导师指导记录
     * @return 结果
     */
    @Override
    public int updateStuMentorshipRecord(StuMentorshipRecord stuMentorshipRecord)
    {
        return stuMentorshipRecordMapper.updateStuMentorshipRecord(stuMentorshipRecord);
    }

    /**
     * 批量删除导师指导记录
     *
     * @param recordIds 需要删除的导师指导记录主键
     * @return 结果
     */
    @Override
    public int deleteStuMentorshipRecordByRecordIds(Integer[] recordIds)
    {
        return stuMentorshipRecordMapper.deleteStuMentorshipRecordByRecordIds(recordIds);
    }

    /**
     * 删除导师指导记录信息
     *
     * @param recordId 导师指导记录主键
     * @return 结果
     */
    @Override
    public int deleteStuMentorshipRecordByRecordId(Integer recordId)
    {
        return stuMentorshipRecordMapper.deleteStuMentorshipRecordByRecordId(recordId);
    }

    @Override
    public AjaxResult checkUnique(StuMentorshipRecord stuMentorshipRecord) {
        boolean exists = stuMentorshipRecordMapper.existsByUniqueFields(
                stuMentorshipRecord.getStudentId(),
                stuMentorshipRecord.getGuidanceTopic(),
                stuMentorshipRecord.getGuidanceTime(),
                stuMentorshipRecord.getGuidanceLocation(),
                stuMentorshipRecord.getSemester(),
                stuMentorshipRecord.getAuditStatus()
        );
        return exists ? AjaxResult.error("已存在相同记录") : AjaxResult.success();
    }

    @Override
    public List<StuMentorshipRecord> selectMentorshipRecordList(StuMentorshipRecord stuMentorshipRecord) {
        return stuMentorshipRecordMapper.selectAuditMentorshipRecordList(stuMentorshipRecord);
    }

    @Override
    public int updateMentorshipAuditInfo(StuMentorshipRecord mentorship) {
        // 1. 获取原始状态
        StuMentorshipRecord originalRecord = stuMentorshipRecordMapper
                .selectStuMentorshipRecordByRecordId(mentorship.getRecordId());
        String beforeStatus = originalRecord.getAuditStatus();

        // 2. 执行审核状态更新
        int updateResult = stuMentorshipRecordMapper.updateMentorshipAuditInfo(mentorship);
        if (updateResult <= 0) {
            throw new ServiceException("审核状态更新失败");
        }

        // 3. 保存审核记录
        saveAuditHistory(
                mentorship.getRecordId(),
                beforeStatus,
                mentorship.getAuditStatus(),
                mentorship.getAuditRemark()
        );

        return updateResult;
    }

    @Override
    public Map<String, Integer> countAuditStatus() {
        try {
            return stuMentorshipRecordMapper.countAuditStatus();
        } catch (Exception e) {
            return new HashMap<String, Integer>(){{
                put("pending", 0);
                put("approved", 0);
                put("rejected", 0);
            }};
        }
    }

    // 新增方法：保存审核历史
    private void saveAuditHistory(Integer mentorshipId, String beforeStatus,
                                  String afterStatus, String remark) {
        AuditHistory history = new AuditHistory();

        // 填充模块信息
        history.setModuleType(4);  // 4代表导师指导模块
        history.setModuleId(mentorshipId.longValue());

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
}
