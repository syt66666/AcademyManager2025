package com.ruoyi.system.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.UserLimit;
import com.ruoyi.system.service.IUserLimitService;

/**
 * 用户取消限制Controller
 */
@RestController
@RequestMapping("/system/userLimit")
public class UserLimitController extends BaseController {
    @Autowired
    private IUserLimitService userLimitService;

    /**
     * 记录取消信息
     */
    @Log(title = "用户取消限制", businessType = BusinessType.INSERT)
    @PostMapping("/recordCancel")
    public AjaxResult recordCancel(@RequestBody UserLimit userLimit) {
        try {
            System.out.println("Controller收到取消记录请求: " + userLimit);
            boolean success = userLimitService.recordCancel(userLimit.getStudentId(), userLimit.getActivityId());
            if (success) {
                System.out.println("Controller: 取消记录成功");
                return AjaxResult.success("取消记录成功");
            } else {
                System.out.println("Controller: 取消记录失败");
                return AjaxResult.error("取消记录失败");
            }
        } catch (Exception e) {
            logger.error("记录取消信息失败", e);
            System.out.println("Controller: 记录取消信息异常: " + e.getMessage());
            return AjaxResult.error("取消记录失败: " + e.getMessage());
        }
    }

    /**
     * 检查取消限制
     */
    @GetMapping("/checkCancelLimit")
    public AjaxResult checkCancelLimit(String studentId) {
        boolean canCancel = userLimitService.checkCanCancel(studentId);
        int cancelCount = userLimitService.getCancelCountThisMonth(studentId);
        System.out.println("Controller checkCancelLimit - 学生ID: " + studentId +
                ", 可取消: " + canCancel + ", 取消次数: " + cancelCount);
        AjaxResult result = AjaxResult.success();
        result.put("canCancel", canCancel);
        result.put("cancelCount", cancelCount);
        // 修改：最大取消次数改为3次
        result.put("maxCancelCount", 3);
        return result;
    }

    /**
     * 获取用户本月取消次数
     */
    @GetMapping("/getCancelCount")
    public AjaxResult getCancelCount(String studentId) {
        int cancelCount = userLimitService.getCancelCountThisMonth(studentId);
        System.out.println("Controller getCancelCount - 学生ID: " + studentId + ", 取消次数: " + cancelCount);
        return AjaxResult.success(cancelCount);
    }

    /**
     * 测试接口：检查取消记录
     */
    @GetMapping("/testCancelRecords")
    public AjaxResult testCancelRecords(String studentId) {
        List<UserLimit> records = userLimitService.selectCancelRecordsByStudentId(studentId);
        System.out.println("Controller testCancelRecords - 学生ID: " + studentId + ", 记录数: " + records.size());
        return AjaxResult.success(records);
    }

    /**
     * 调试接口：查询所有记录
     */
    @GetMapping("/debugAllRecords")
    public AjaxResult debugAllRecords() {
        List<UserLimit> records = userLimitService.selectAllRecords();
        System.out.println("Controller debugAllRecords - 总记录数: " + records.size());
        return AjaxResult.success(records);
    }

    /**
     * 调试接口：手动插入测试记录
     */
    @GetMapping("/insertTestRecord")
    public AjaxResult insertTestRecord(String studentId, Long activityId) {
        try {
            boolean success = userLimitService.recordCancel(studentId, activityId);
            if (success) {
                return AjaxResult.success("测试记录插入成功");
            } else {
                return AjaxResult.error("测试记录插入失败");
            }
        } catch (Exception e) {
            logger.error("插入测试记录失败", e);
            return AjaxResult.error("插入测试记录失败: " + e.getMessage());
        }
    }
}
