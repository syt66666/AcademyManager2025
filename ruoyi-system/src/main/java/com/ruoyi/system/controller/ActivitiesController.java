package com.ruoyi.system.controller;

import java.util.List;
import java.util.Map;

import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.system.domain.StuCourse;
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
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

/**
 * 活动信息 Controller
 */
@RestController
@RequestMapping("/system/activities")
public class ActivitiesController extends BaseController {
    @Autowired
    private IActivitiesService activityService;

    /**
     * 查询活动列表
     */
    @GetMapping("/list")
    public TableDataInfo list(Activities activity) {
        startPage();
        List<Activities> list = activityService.selectActivityList(activity);
        return getDataTable(list);
    }

    /**
     * 导出活动列表
     */
    @Log(title = "导出数据", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Activities activities) {
        List<Activities> list = activityService.selectActivityList(activities);
        ExcelUtil<Activities> util = new ExcelUtil<Activities>(Activities.class);
        util.exportExcel(response, list, "导出数据");
    }

    /**
     * 获取活动详细信息
     */
    @GetMapping(value = "/{activityId}")
    public AjaxResult getInfo(@PathVariable("activityId") Integer activityId) {
        return AjaxResult.success(activityService.selectActivityById(activityId));
    }

    /**
     * 新增活动
     */
    @Log(title = "活动信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Activities activity) {
        return toAjax(activityService.insertActivity(activity));
    }

    /**
     * 修改活动
     */
    @Log(title = "活动信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Activities activity) {
        return toAjax(activityService.updateActivity(activity));
    }

    /**
     * 删除活动
     */
    @Log(title = "活动信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{activityIds}")
    public AjaxResult remove(@PathVariable Integer[] activityIds) {
        return toAjax(activityService.deleteActivityByIds(activityIds));
    }

    /**
     * 导入活动列表
     */
    @Log(title = "活动信息管理", businessType = BusinessType.IMPORT)
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception {
        ExcelUtil<Activities> util = new ExcelUtil<Activities>(Activities.class);
        List<Activities> userList = util.importExcel(file.getInputStream());
        LoginUser loginUser = getLoginUser();
        String operName = loginUser.getUsername();
        String message = activityService.importActivity(userList, updateSupport, operName);
        return AjaxResult.success(message);
    }

    @GetMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) {
        ExcelUtil<Activities> util = new ExcelUtil<>(Activities.class);
        util.importTemplateExcel(response, "活动数据");
    }

    /**
     * 报名
     *
     * @param params
     * @return
     */
    @PostMapping("/signUpCapacity")
    public AjaxResult signUpCapacity(@RequestBody Map<String, Integer> params) {
        Integer activityId = params.get("activityId");
        int result = activityService.decreaseCapacity(activityId);
        return result > 0 ? AjaxResult.success("报名成功") : AjaxResult.error("报名失败");
    }

    @PostMapping("/cancelSignUpCapacity")
    public AjaxResult cancelSignUpCapacity(@RequestBody Map<String, Integer> params) {
        Integer activityId = params.get("activityId");
        int result = activityService.increaseCapacity(activityId);
        return result > 0 ? AjaxResult.success("取消报名成功") : AjaxResult.error("取消报名失败");
    }
}


