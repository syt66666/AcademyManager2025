package com.ruoyi.system.controller;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.service.ICourseStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 课程统计 Controller
 */
@RestController
@RequestMapping("/system/courseStatistics")
public class CourseStatisticsController extends BaseController {

    @Autowired
    private ICourseStatisticsService courseStatisticsService;

    /**
     * 获取课程统计概览
     */
    @GetMapping("/overview")
    public AjaxResult getCourseOverview() {
        Map<String, Object> overview = courseStatisticsService.getCourseOverview();
        return AjaxResult.success(overview);
    }

    /**
     * 获取各书院课程统计
     */
    @GetMapping("/collegeStats")
    public AjaxResult getCollegeCourseStats() {
        Map<String, Object> collegeStats = courseStatisticsService.getCollegeCourseStats();
        return AjaxResult.success(collegeStats);
    }

    /**
     * 获取课程类型分布
     */
    @GetMapping("/typeDistribution")
    public AjaxResult getCourseTypeDistribution() {
        Map<String, Object> typeDistribution = courseStatisticsService.getCourseTypeDistribution();
        return AjaxResult.success(typeDistribution);
    }

    /**
     * 获取最近课程列表
     */
    @GetMapping("/recentCourses")
    public AjaxResult getRecentCourses() {
        Map<String, Object> recentCourses = courseStatisticsService.getRecentCourses();
        return AjaxResult.success(recentCourses);
    }

    /**
     * 根据书院筛选课程统计
     */
    @GetMapping("/statsByCollege")
    public AjaxResult getCourseStatsByCollege(@RequestParam String college) {
        Map<String, Object> stats = courseStatisticsService.getCourseStatsByCollege(college);
        return AjaxResult.success(stats);
    }

    /**
     * 获取书院课程列表
     */
    @GetMapping("/collegeCourses")
    public AjaxResult getCollegeCourses(@RequestParam String college) {
        Map<String, Object> courses = courseStatisticsService.getCollegeCourses(college);
        return AjaxResult.success(courses);
    }
}
