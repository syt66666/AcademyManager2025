package com.ruoyi.system.controller;

import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.domain.dto.ActivityAuditDTO;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
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
import com.ruoyi.system.domain.StuActivityRecord;
import com.ruoyi.system.service.IStuActivityRecordService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

@RestController
@RequestMapping("/system/activity")
public class StuActivityRecordController extends BaseController
{
    @Autowired
    private IStuActivityRecordService stuActivityRecordService;

    /**
     * 查询学生文体活动记录列表
     */
//    @PreAuthorize("@ss.hasPermi('system:activity:list')")
    @GetMapping("/list")
    public TableDataInfo list(StuActivityRecord stuActivityRecord)
    {
        startPage();
        List<StuActivityRecord> list = stuActivityRecordService.selectStuActivityRecordList(stuActivityRecord);
        return getDataTable(list);
    }

    /**
     * 导出学生文体活动记录列表
     */
    @PreAuthorize("@ss.hasPermi('system:activity:export')")
    @Log(title = "学生文体活动记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, StuActivityRecord stuActivityRecord)
    {
        List<StuActivityRecord> list = stuActivityRecordService.selectStuActivityRecordList(stuActivityRecord);
        ExcelUtil<StuActivityRecord> util = new ExcelUtil<StuActivityRecord>(StuActivityRecord.class);
        util.exportExcel(response, list, "学生文体活动记录数据");
    }

    /**
     * 获取学生文体活动记录详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:activity:query')")
    @GetMapping(value = "/{activityId}")
    public AjaxResult getInfo(@PathVariable("activityId") Integer activityId)
    {
        return success(stuActivityRecordService.selectStuActivityRecordByActivityId(activityId));
    }

    /**
     * 新增学生文体活动记录
     */
//    @PreAuthorize("@ss.hasPermi('system:activity:add')")
    @Log(title = "学生文体活动记录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody StuActivityRecord stuActivityRecord)
    {
        return toAjax(stuActivityRecordService.insertStuActivityRecord(stuActivityRecord));
    }

    /**
     * 修改学生文体活动记录
     */
//    @PreAuthorize("@ss.hasPermi('system:activity:edit')")
    @Log(title = "学生文体活动记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody StuActivityRecord stuActivityRecord)
    {
        return toAjax(stuActivityRecordService.updateStuActivityRecord(stuActivityRecord));
    }

    /**
     * 删除学生文体活动记录
     */
//    @PreAuthorize("@ss.hasPermi('system:activity:remove')")
    @Log(title = "学生文体活动记录", businessType = BusinessType.DELETE)
    @DeleteMapping("/{activityIds}")
    public AjaxResult remove(@PathVariable Integer[] activityIds)
    {
        return toAjax(stuActivityRecordService.deleteStuActivityRecordByActivityIds(activityIds));
    }

    /**
     * 更新审核信息
     */
    @PreAuthorize("@ss.hasPermi('system:activity:audit')")
    @Log(title = "文体活动审核", businessType = BusinessType.UPDATE)
    @PutMapping("/audit")
    public AjaxResult auditActivity(@Validated @RequestBody ActivityAuditDTO auditDTO) {
        // 构建更新参数
        StuActivityRecord activity = new StuActivityRecord();
        activity.setActivityId(auditDTO.getActivityId());
        activity.setAuditStatus(auditDTO.getAuditStatus());
        activity.setAuditRemark(auditDTO.getAuditRemark());
        activity.setAuditTime(new Date());
        // 执行更新操作
        return toAjax(stuActivityRecordService.updateActivityAuditInfo(activity));
    }

}
