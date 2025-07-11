package com.ruoyi.system.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.system.domain.Activities;
import com.ruoyi.system.service.IActivitiesService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/system/activities")
public class ActivitiesController extends BaseController {

    @Autowired
    private IActivitiesService activitiesService;

    /**
     * 查询活动列表
     */
    @GetMapping("/list")
    public TableDataInfo list(Activities activities) {
        startPage();
        List<Activities> list = activitiesService.selectActivitiesList(activities);
        return getDataTable(list);
    }

    /**
     * 导出活动信息
     */
    @Log(title = "活动信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Activities activities) {
        List<Activities> list = activitiesService.selectActivitiesList(activities);
        ExcelUtil<Activities> util = new ExcelUtil<>(Activities.class);
        util.exportExcel(response, list, "活动信息数据");
    }

    /**
     * 获取活动详细信息
     */
    @GetMapping(value = "/{activityId}")
    public AjaxResult getInfo(@PathVariable("activityId") Long activityId) {
        return success(activitiesService.selectActivitiesById(activityId));
    }

    /**
     * 新增活动
     */
    @Log(title = "活动信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Activities activities) {
        return toAjax(activitiesService.insertActivities(activities));
    }

    /**
     * 修改活动
     */
    @Log(title = "活动信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Activities activities) {
        return toAjax(activitiesService.updateActivities(activities));
    }

    /**
     * 删除活动
     */
    @Log(title = "活动信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{activityIds}")
    public AjaxResult remove(@PathVariable Long[] activityIds) {
        return toAjax(activitiesService.deleteActivitiesByIds(activityIds));
    }

    /**
     * 导入活动数据
     */
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception {
        ExcelUtil<Activities> util = new ExcelUtil<>(Activities.class);
        List<Activities> activityList = util.importExcel(file.getInputStream());
        String operName = getLoginUser().getUsername();
        String message = activitiesService.importActivities(activityList, updateSupport, operName);
        return AjaxResult.success(message);
    }

    /**
     * 下载导入模板
     */
    @GetMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) {
        ExcelUtil<Activities> util = new ExcelUtil<>(Activities.class);
        util.importTemplateExcel(response, "活动数据模板");
    }
}
