package com.ruoyi.system.controller;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.service.IActivityStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 活动统计 Controller
 */
@RestController
@RequestMapping("/system/activityStatistics")
public class ActivityStatisticsController extends BaseController {

    @Autowired
    private IActivityStatisticsService activityStatisticsService;

    /**
     * 获取活动统计概览
     */
    @GetMapping("/overview")
    public AjaxResult getActivityOverview() {
        Map<String, Object> overview = activityStatisticsService.getActivityOverview();
        return AjaxResult.success(overview);
    }

    /**
     * 获取各书院活动统计
     */
    @GetMapping("/collegeStats")
    public AjaxResult getCollegeActivityStats() {
        Map<String, Object> collegeStats = activityStatisticsService.getCollegeActivityStats();
        return AjaxResult.success(collegeStats);
    }

    /**
     * 获取活动类型分布
     */
    @GetMapping("/typeDistribution")
    public AjaxResult getActivityTypeDistribution() {
        Map<String, Object> typeDistribution = activityStatisticsService.getActivityTypeDistribution();
        return AjaxResult.success(typeDistribution);
    }

    /**
     * 获取最近活动列表
     */
    @GetMapping("/recentActivities")
    public AjaxResult getRecentActivities(@RequestParam(defaultValue = "10") int limit) {
        Map<String, Object> recentActivities = activityStatisticsService.getRecentActivities(limit);
        return AjaxResult.success(recentActivities);
    }

    /**
     * 根据书院筛选活动统计
     */
    @GetMapping("/collegeFilter")
    public AjaxResult getActivityStatsByCollege(@RequestParam String college) {
        Map<String, Object> stats = activityStatisticsService.getActivityStatsByCollege(college);
        return AjaxResult.success(stats);
    }

    /**
     * 获取书院活动列表
     */
    @GetMapping("/collegeActivities")
    public AjaxResult getCollegeActivities(@RequestParam String college) {
        Map<String, Object> activities = activityStatisticsService.getCollegeActivities(college);
        return AjaxResult.success(activities);
    }
}
