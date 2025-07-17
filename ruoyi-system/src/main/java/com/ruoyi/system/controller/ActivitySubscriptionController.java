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
import com.ruoyi.system.domain.ActivitySubscription;
import com.ruoyi.system.service.IActivitySubscriptionService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 用户活动订阅Controller
 *
 * @author ruoyi
 * @date 2025-07-17
 */
@RestController
@RequestMapping("/system/subscription")
public class ActivitySubscriptionController extends BaseController
{
    @Autowired
    private IActivitySubscriptionService activitySubscriptionService;

    /**
     * 查询用户活动订阅列表
     */
    @PreAuthorize("@ss.hasPermi('system:subscription:list')")
    @GetMapping("/list")
    public TableDataInfo list(ActivitySubscription activitySubscription)
    {
        startPage();
        List<ActivitySubscription> list = activitySubscriptionService.selectActivitySubscriptionList(activitySubscription);
        return getDataTable(list);
    }

    /**
     * 导出用户活动订阅列表
     */
    @PreAuthorize("@ss.hasPermi('system:subscription:export')")
    @Log(title = "用户活动订阅", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, ActivitySubscription activitySubscription)
    {
        List<ActivitySubscription> list = activitySubscriptionService.selectActivitySubscriptionList(activitySubscription);
        ExcelUtil<ActivitySubscription> util = new ExcelUtil<ActivitySubscription>(ActivitySubscription.class);
        util.exportExcel(response, list, "用户活动订阅数据");
    }

    /**
     * 获取用户活动订阅详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:subscription:query')")
    @GetMapping(value = "/{subscriptionId}")
    public AjaxResult getInfo(@PathVariable("subscriptionId") Long subscriptionId)
    {
        return success(activitySubscriptionService.selectActivitySubscriptionBySubscriptionId(subscriptionId));
    }

    /**
     * 新增用户活动订阅
     */
    @PreAuthorize("@ss.hasPermi('system:subscription:add')")
    @Log(title = "用户活动订阅", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody ActivitySubscription activitySubscription)
    {
        return toAjax(activitySubscriptionService.insertActivitySubscription(activitySubscription));
    }

    /**
     * 修改用户活动订阅
     */
    @PreAuthorize("@ss.hasPermi('system:subscription:edit')")
    @Log(title = "用户活动订阅", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ActivitySubscription activitySubscription)
    {
        return toAjax(activitySubscriptionService.updateActivitySubscription(activitySubscription));
    }

    /**
     * 删除用户活动订阅
     */
    @PreAuthorize("@ss.hasPermi('system:subscription:remove')")
    @Log(title = "用户活动订阅", businessType = BusinessType.DELETE)
    @DeleteMapping("/{subscriptionIds}")
    public AjaxResult remove(@PathVariable Long[] subscriptionIds)
    {
        return toAjax(activitySubscriptionService.deleteActivitySubscriptionBySubscriptionIds(subscriptionIds));
    }
}
