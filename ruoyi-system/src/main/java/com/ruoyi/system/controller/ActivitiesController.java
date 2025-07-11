package com.ruoyi.system.controller;

import java.util.List;
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
import com.ruoyi.system.domain.Activities;
import com.ruoyi.system.service.IActivitiesService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 活动信息 Controller
 */
@RestController
@RequestMapping("/system/activity")
public class ActivitiesController extends BaseController
{
    @Autowired
    private IActivitiesService activityService;

    /**
     * 查询活动列表
     */
    @PreAuthorize("@ss.hasPermi('system:activity:list')")
    @GetMapping("/list")
    public TableDataInfo list(Activities activity)
    {
        startPage();
        List<Activities> list = activityService.selectActivityList(activity);
        return getDataTable(list);
    }

    /**
     * 导出活动列表
     */
    @PreAuthorize("@ss.hasPermi('system:activity:export')")
    @Log(title = "活动信息", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(Activities activity)
    {
        List<Activities> list = activityService.selectActivityList(activity);
        ExcelUtil<Activities> util = new ExcelUtil<Activities>(Activities.class);
        return util.exportExcel(list, "activity");
    }

    /**
     * 获取活动详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:activity:query')")
    @GetMapping(value = "/{activityId}")
    public AjaxResult getInfo(@PathVariable("activityId") Integer activityId)
    {
        return AjaxResult.success(activityService.selectActivityById(activityId));
    }

    /**
     * 新增活动
     */
    @PreAuthorize("@ss.hasPermi('system:activity:add')")
    @Log(title = "活动信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Activities activity)
    {
        return toAjax(activityService.insertActivity(activity));
    }

    /**
     * 修改活动
     */
    @PreAuthorize("@ss.hasPermi('system:activity:edit')")
    @Log(title = "活动信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Activities activity)
    {
        return toAjax(activityService.updateActivity(activity));
    }

    /**
     * 删除活动
     */
    @PreAuthorize("@ss.hasPermi('system:activity:remove')")
    @Log(title = "活动信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{activityIds}")
    public AjaxResult remove(@PathVariable Integer[] activityIds)
    {
        return toAjax(activityService.deleteActivityByIds(activityIds));
    }
}
