package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.AuditHistoryMapper;
import com.ruoyi.system.domain.AuditHistory;
import com.ruoyi.system.service.IAuditHistoryService;


@Service
public class AuditHistoryServiceImpl implements IAuditHistoryService
{
    @Autowired
    private AuditHistoryMapper auditHistoryMapper;

    /**
     * 查询统一审核历史记录
     *
     * @param id 统一审核历史记录主键
     * @return 统一审核历史记录
     */
    @Override
    public AuditHistory selectAuditHistoryById(Long id)
    {
        return auditHistoryMapper.selectAuditHistoryById(id);
    }

    /**
     * 查询统一审核历史记录列表
     *
     * @param auditHistory 统一审核历史记录
     * @return 统一审核历史记录
     */
    @Override
    public List<AuditHistory> selectAuditHistoryList(AuditHistory auditHistory)
    {
        return auditHistoryMapper.selectAuditHistoryList(auditHistory);
    }

    /**
     * 新增统一审核历史记录
     *
     * @param auditHistory 统一审核历史记录
     * @return 结果
     */
    @Override
    public int insertAuditHistory(AuditHistory auditHistory)
    {
        return auditHistoryMapper.insertAuditHistory(auditHistory);
    }

    /**
     * 修改统一审核历史记录
     *
     * @param auditHistory 统一审核历史记录
     * @return 结果
     */
    @Override
    public int updateAuditHistory(AuditHistory auditHistory)
    {
        return auditHistoryMapper.updateAuditHistory(auditHistory);
    }

    /**
     * 批量删除统一审核历史记录
     *
     * @param ids 需要删除的统一审核历史记录主键
     * @return 结果
     */
    @Override
    public int deleteAuditHistoryByIds(Long[] ids)
    {
        return auditHistoryMapper.deleteAuditHistoryByIds(ids);
    }

    /**
     * 删除统一审核历史记录信息
     *
     * @param id 统一审核历史记录主键
     * @return 结果
     */
    @Override
    public int deleteAuditHistoryById(Long id)
    {
        return auditHistoryMapper.deleteAuditHistoryById(id);
    }
}
