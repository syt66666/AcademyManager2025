package com.ruoyi.system.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.AuditHistory;
import com.ruoyi.system.service.IAuditHistoryService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;
@RestController
@RequestMapping("/system/history")
public class AuditHistoryController extends BaseController
{
    @Autowired
    private IAuditHistoryService auditHistoryService;

    /**
     * 查询统一审核历史记录列表
     */
    @GetMapping("/list")
    public TableDataInfo list(AuditHistory auditHistory)
    {
        startPage();
        List<AuditHistory> list = auditHistoryService.selectAuditHistoryList(auditHistory);
        return getDataTable(list);
    }

    /**
     * 导出统一审核历史记录列表
     */
    @PreAuthorize("@ss.hasPermi('system:history:export')")
    @Log(title = "统一审核历史记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, AuditHistory auditHistory)
    {
        List<AuditHistory> list = auditHistoryService.selectAuditHistoryList(auditHistory);
        ExcelUtil<AuditHistory> util = new ExcelUtil<AuditHistory>(AuditHistory.class);
        util.exportExcel(response, list, "统一审核历史记录数据");
    }

    /**
     * 获取统一审核历史记录详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:history:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(auditHistoryService.selectAuditHistoryById(id));
    }

    /**
     * 新增统一审核历史记录
     */
    @PreAuthorize("@ss.hasPermi('system:history:add')")
    @Log(title = "统一审核历史记录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody AuditHistory auditHistory)
    {
        return toAjax(auditHistoryService.insertAuditHistory(auditHistory));
    }

    /**
     * 修改统一审核历史记录
     */
    @PreAuthorize("@ss.hasPermi('system:history:edit')")
    @Log(title = "统一审核历史记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody AuditHistory auditHistory)
    {
        return toAjax(auditHistoryService.updateAuditHistory(auditHistory));
    }

    /**
     * 删除统一审核历史记录
     */
    @PreAuthorize("@ss.hasPermi('system:history:remove')")
    @Log(title = "统一审核历史记录", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(auditHistoryService.deleteAuditHistoryByIds(ids));
    }
}
