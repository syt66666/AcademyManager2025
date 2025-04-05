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
import com.ruoyi.system.mapper.StuCompetitionRecordMapper;
import com.ruoyi.system.domain.StuCompetitionRecord;
import com.ruoyi.system.service.IStuCompetitionRecordService;
import org.springframework.transaction.annotation.Transactional;


@Service
public class StuCompetitionRecordServiceImpl implements IStuCompetitionRecordService {
    @Autowired
    private StuCompetitionRecordMapper stuCompetitionRecordMapper;
    @Autowired
    private AuditHistoryMapper auditHistoryMapper;
    @Autowired
    private ISysUserService userService;

    /**
     * 查询学生科创竞赛记录
     *
     * @param competitionId 学生科创竞赛记录主键
     * @return 学生科创竞赛记录
     */
    @Override
    public StuCompetitionRecord selectStuCompetitionRecordByCompetitionId(Integer competitionId) {
        return stuCompetitionRecordMapper.selectStuCompetitionRecordByCompetitionId(competitionId);
    }

    /**
     * 查询学生科创竞赛记录列表
     *
     * @param stuCompetitionRecord 学生科创竞赛记录
     * @return 学生科创竞赛记录
     */
    @Override
    public List<StuCompetitionRecord> selectStuCompetitionRecordList(StuCompetitionRecord stuCompetitionRecord) {
        return stuCompetitionRecordMapper.selectStuCompetitionRecordList(stuCompetitionRecord);
    }

    /**
     * 新增学生科创竞赛记录
     *
     * @param stuCompetitionRecord 学生科创竞赛记录
     * @return 结果
     */
    @Override
    public int insertStuCompetitionRecord(StuCompetitionRecord stuCompetitionRecord) {
        int score = calculateCompetitionScore(stuCompetitionRecord);
        stuCompetitionRecord.setScholarshipPoints((long) score);
        return stuCompetitionRecordMapper.insertStuCompetitionRecord(stuCompetitionRecord);
    }

    public int calculateCompetitionScore(StuCompetitionRecord record) {
        // 1. 活动级别基础分判断（类似成绩分布的基础分逻辑）
        int baseScore = 0;
        String competitionLevel = record.getCompetitionLevel();
        if ("国际级".equals(competitionLevel)) {
            baseScore = 100;
        } else if ("国家级".equals(competitionLevel)) {
            baseScore = 80;
        } else if ("省级".equals(competitionLevel)) {
            baseScore = 60;
        } else if ("校级".equals(competitionLevel)) {
            baseScore = 40;
        } else if ("院级".equals(competitionLevel)) {
            baseScore = 20;
        }

        // 2. 奖项等级系数判断（类似不同等级的权重调整）
        double coefficient = 0.0;
        String awardLevel = record.getAwardLevel();
        if ("特等奖".equals(awardLevel)) {
            coefficient = 1.0;
        } else if ("一等奖".equals(awardLevel)) {
            coefficient = 0.8;
        } else if ("二等奖".equals(awardLevel)) {
            coefficient = 0.6;
        } else if ("三等奖".equals(awardLevel)) {
            coefficient = 0.4;
        } else if ("优秀奖".equals(awardLevel)) {
            coefficient = 0.2;
        }

        // 4. 综合计算（类似成绩分布的总分计算）
        int totalScore = (int) Math.round(baseScore * coefficient);

        // 5. 最低保障分（类似成绩分布的保底逻辑）
        return Math.max(totalScore, 5);
    }

    /**
     * 修改学生科创竞赛记录
     *
     * @param stuCompetitionRecord 学生科创竞赛记录
     * @return 结果
     */
    @Override
    public int updateStuCompetitionRecord(StuCompetitionRecord stuCompetitionRecord) {
        int score = calculateCompetitionScore(stuCompetitionRecord);
        stuCompetitionRecord.setScholarshipPoints((long) score);
        return stuCompetitionRecordMapper.updateStuCompetitionRecord(stuCompetitionRecord);
    }

    /**
     * 批量删除学生科创竞赛记录
     *
     * @param competitionIds 需要删除的学生科创竞赛记录主键
     * @return 结果
     */
    @Override
    public int deleteStuCompetitionRecordByCompetitionIds(Integer[] competitionIds) {
        return stuCompetitionRecordMapper.deleteStuCompetitionRecordByCompetitionIds(competitionIds);
    }

    /**
     * 删除学生科创竞赛记录信息
     *
     * @param competitionId 学生科创竞赛记录主键
     * @return 结果
     */
    @Override
    public int deleteStuCompetitionRecordByCompetitionId(Integer competitionId) {
        return stuCompetitionRecordMapper.deleteStuCompetitionRecordByCompetitionId(competitionId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class) // 添加事务管理
    public int updateCompetitionAuditInfo(StuCompetitionRecord competition) {
        // 1. 获取原始状态
        StuCompetitionRecord originalRecord = stuCompetitionRecordMapper
                .selectStuCompetitionRecordByCompetitionId(competition.getCompetitionId());
        String beforeStatus = originalRecord.getAuditStatus();

        // 2. 执行审核状态更新
        int updateResult = stuCompetitionRecordMapper.updateCompetitionAuditInfo(competition);
        if (updateResult <= 0) {
            throw new ServiceException("审核状态更新失败");
        }

        // 3. 保存审核记录
        saveAuditHistory(
                competition.getCompetitionId(),
                beforeStatus,
                competition.getAuditStatus(),
                competition.getAuditRemark()
        );

        return updateResult;
    }

    // 新增方法：保存审核历史
    private void saveAuditHistory(Integer competitionId, String beforeStatus,
                                  String afterStatus, String remark) {
        AuditHistory history = new AuditHistory();

        // 填充模块信息
        history.setModuleType(1);  // 1代表科创竞赛模块
        history.setModuleId(competitionId.longValue());

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
    public List<StuCompetitionRecord> selectCompetitionRecordList(StuCompetitionRecord stuCompetitionRecord) {
        return stuCompetitionRecordMapper.selectAuditCompetitionRecordList(stuCompetitionRecord);
    }

    @Override
    public AjaxResult checkUnique(StuCompetitionRecord competition) {
        boolean exists = stuCompetitionRecordMapper.existsByUniqueFields(
                competition.getStudentId(),
                competition.getCompetitionName(),
                competition.getCompetitionLevel(),
                competition.getAwardLevel(),
                competition.getSemester()
        );
        return exists ? AjaxResult.error("已存在相同记录") : AjaxResult.success();
    }

    @Override
    public Map<String, Integer> countAuditStatus() {
        try {
            return stuCompetitionRecordMapper.countAuditStatus();
        } catch (Exception e) {
            return new HashMap<String, Integer>() {{
                put("pending", 0);
                put("approved", 0);
                put("rejected", 0);
            }};
        }
    }
}
