package com.ruoyi.system.service;

import java.util.Map;

/**
 * 课程统计服务接口
 */
public interface ICourseStatisticsService {

    /**
     * 获取课程统计概览
     * @return 统计概览数据
     */
    Map<String, Object> getCourseOverview();

    /**
     * 获取各书院课程统计
     * @return 各书院统计数据
     */
    Map<String, Object> getCollegeCourseStats();

    /**
     * 获取课程类型分布
     * @return 课程类型分布数据
     */
    Map<String, Object> getCourseTypeDistribution();

    /**
     * 获取最近课程列表
     * @return 最近课程数据
     */
    Map<String, Object> getRecentCourses();

    /**
     * 根据书院筛选课程统计
     * @param college 书院名称
     * @return 筛选后的统计数据
     */
    Map<String, Object> getCourseStatsByCollege(String college);

    /**
     * 获取书院课程列表
     * @param college 书院名称
     * @return 书院课程列表
     */
    Map<String, Object> getCollegeCourses(String college);
}
