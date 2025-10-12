package com.ruoyi.system.service;

import java.util.Map;

/**
 * 活动统计服务接口
 */
public interface IActivityStatisticsService {

    /**
     * 获取活动统计概览
     * @return 统计概览数据
     */
    Map<String, Object> getActivityOverview();

    /**
     * 获取各书院活动统计
     * @return 各书院统计数据
     */
    Map<String, Object> getCollegeActivityStats();

    /**
     * 获取活动类型分布
     * @return 活动类型分布数据
     */
    Map<String, Object> getActivityTypeDistribution();

    /**
     * 获取最近活动列表
     * @param limit 限制数量
     * @return 最近活动数据
     */
    Map<String, Object> getRecentActivities(int limit);

    /**
     * 根据书院筛选活动统计
     * @param college 书院名称
     * @return 筛选后的统计数据
     */
    Map<String, Object> getActivityStatsByCollege(String college);

    /**
     * 获取书院活动列表
     * @param college 书院名称
     * @return 书院活动列表
     */
    Map<String, Object> getCollegeActivities(String college);
}
