package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.AuditHistory;


public interface AuditHistoryMapper
{
    /**
     * 查询统一审核历史记录
     *
     * @param id 统一审核历史记录主键
     * @return 统一审核历史记录
     */
    public AuditHistory selectAuditHistoryById(Long id);

    /**
     * 查询统一审核历史记录列表
     *
     * @param auditHistory 统一审核历史记录
     * @return 统一审核历史记录集合
     */
    public List<AuditHistory> selectAuditHistoryList(AuditHistory auditHistory);

    /**
     * 新增统一审核历史记录
     *
     * @param auditHistory 统一审核历史记录
     * @return 结果
     */
    public int insertAuditHistory(AuditHistory auditHistory);

    /**
     * 修改统一审核历史记录
     *
     * @param auditHistory 统一审核历史记录
     * @return 结果
     */
    public int updateAuditHistory(AuditHistory auditHistory);

    /**
     * 删除统一审核历史记录
     *
     * @param id 统一审核历史记录主键
     * @return 结果
     */
    public int deleteAuditHistoryById(Long id);

    /**
     * 批量删除统一审核历史记录
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteAuditHistoryByIds(Long[] ids);
}
