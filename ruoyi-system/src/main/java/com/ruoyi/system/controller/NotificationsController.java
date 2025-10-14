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
import com.ruoyi.system.domain.Notifications;
import com.ruoyi.system.service.INotificationsService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 系统通知公告Controller
 * 
 * @author ruoyi
 * @date 2025-10-12
 */
@RestController
@RequestMapping("/system/notifications")
public class NotificationsController extends BaseController
{
    @Autowired
    private INotificationsService notificationsService;

    /**
     * 查询系统通知公告列表
     */
    @GetMapping("/list")
    public TableDataInfo list(Notifications notifications)
    {
        startPage();
        List<Notifications> list = notificationsService.selectNotificationsList(notifications);
        return getDataTable(list);
    }

    /**
     * 查询系统通知公告列表（学生用户无需权限）
     */
    @GetMapping("/student/list")
    public TableDataInfo studentList(Notifications notifications)
    {
        startPage();
        List<Notifications> list = notificationsService.selectNotificationsList(notifications);
        return getDataTable(list);
    }

    /**
     * 查询系统通知公告列表（公开接口，完全绕过权限检查）
     */
    @GetMapping("/public/list")
    public TableDataInfo publicList(Notifications notifications)
    {
        startPage();
        List<Notifications> list = notificationsService.selectNotificationsList(notifications);
        return getDataTable(list);
    }

    /**
     * 导出系统通知公告列表
     */

    @Log(title = "系统通知公告", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Notifications notifications)
    {
        List<Notifications> list = notificationsService.selectNotificationsList(notifications);
        ExcelUtil<Notifications> util = new ExcelUtil<Notifications>(Notifications.class);
        util.exportExcel(response, list, "系统通知公告数据");
    }

    /**
     * 获取系统通知公告详细信息
     */

    @GetMapping(value = "/{notiId}")
    public AjaxResult getInfo(@PathVariable("notiId") Long notiId)
    {
        return success(notificationsService.selectNotificationsByNotiId(notiId));
    }

    /**
     * 新增系统通知公告
     */

    @Log(title = "系统通知公告", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Notifications notifications)
    {
        return toAjax(notificationsService.insertNotifications(notifications));
    }

    /**
     * 修改系统通知公告
     */

    @Log(title = "系统通知公告", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Notifications notifications)
    {
        return toAjax(notificationsService.updateNotifications(notifications));
    }

    /**
     * 删除系统通知公告
     */
    @Log(title = "系统通知公告", businessType = BusinessType.DELETE)
	@DeleteMapping("/{notiIds}")
    public AjaxResult remove(@PathVariable Long[] notiIds)
    {
        return toAjax(notificationsService.deleteNotificationsByNotiIds(notiIds));
    }
}
