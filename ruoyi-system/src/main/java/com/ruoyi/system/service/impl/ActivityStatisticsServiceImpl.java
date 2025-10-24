package com.ruoyi.system.service.impl;

import com.ruoyi.system.domain.Activities;
import com.ruoyi.system.mapper.ActivitiesMapper;
import com.ruoyi.system.service.IActivityStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 活动统计服务实现
 */
@Service
public class ActivityStatisticsServiceImpl implements IActivityStatisticsService {

    @Autowired
    private ActivitiesMapper activitiesMapper;

    @Override
    public Map<String, Object> getActivityOverview() {
        Map<String, Object> result = new HashMap<>();
        
        // 获取总活动数
        Activities queryActivity = new Activities();
        List<Activities> allActivities = activitiesMapper.selectActivityList(queryActivity);
        int totalActivities = allActivities.size();
        
        // 计算进行中的活动数（当前时间在报名开始和活动结束之间）、已完成活动数和总参与人数
        Date now = new Date();
        int ongoingActivities = 0;
        int completedActivities = 0;
        int totalParticipants = 0;
        
        for (Activities activity : allActivities) {
            if (activity.getActivityStart() != null && activity.getEndTime() != null) {
                if (now.after(activity.getActivityStart()) && now.before(activity.getEndTime())) {
                    ongoingActivities++;
                } else if (now.after(activity.getEndTime())) {
                    completedActivities++;
                }
            }
            // 累加参与人数（activityCapacity = 已选人数）
            totalParticipants += (activity.getActivityCapacity() != null ? activity.getActivityCapacity() : 0);
        }
        
        result.put("totalActivities", totalActivities);
        result.put("ongoingActivities", ongoingActivities);
        result.put("completedActivities", completedActivities);
        result.put("totalParticipants", totalParticipants);
        
        return result;
    }

    @Override
    public Map<String, Object> getCollegeActivityStats() {
        Map<String, Object> result = new HashMap<>();
        
        // 各书院活动数据
        Map<String, Map<String, Object>> collegeData = new HashMap<>();
        
        // 书院列表
        String[] colleges = {"大煜书院", "伯川书院", "令希书院", "厚德书院", "知行书院", "笃学书院", "求实书院"};
        
        for (String college : colleges) {
            Map<String, Object> collegeStats = new HashMap<>();
            
            // 查询该书院的活动数量
            Activities queryActivity = new Activities();
            queryActivity.setOrganizer(college);
            List<Activities> collegeActivities = activitiesMapper.selectActivityList(queryActivity);
            int totalActivities = collegeActivities.size();
            
            // 计算进行中的活动数和参与人数
            Date now = new Date();
            int ongoingActivities = 0;
            int participants = 0;
            for (Activities activity : collegeActivities) {
                // 统计进行中的活动
                if (activity.getActivityStart() != null && activity.getEndTime() != null) {
                    if (now.after(activity.getActivityStart()) && now.before(activity.getEndTime())) {
                        ongoingActivities++;
                    }
                }
                // 累加参与人数（activityCapacity = 已选人数）
                participants += (activity.getActivityCapacity() != null ? activity.getActivityCapacity() : 0);
            }
            
            collegeStats.put("totalActivities", totalActivities);
            collegeStats.put("ongoingActivities", ongoingActivities);
            collegeStats.put("totalParticipants", participants);
            
            collegeData.put(college, collegeStats);
        }
        
        result.put("collegeData", collegeData);
        return result;
    }

    @Override
    public Map<String, Object> getActivityTypeDistribution() {
        Map<String, Object> result = new HashMap<>();
        
        // 活动类型映射
        Map<String, String> typeMap = new HashMap<>();
        typeMap.put("1", "人格塑造与价值引领活动类");
        typeMap.put("2", "知识融合与思维进阶活动类");
        typeMap.put("3", "能力锻造与实践创新活动类");
        typeMap.put("4", "社会责任与领军意识活动类");
        
        List<Map<String, Object>> typeDistribution = new ArrayList<>();
        int totalActivities = 0;
        
        // 统计各类型活动数量
        Map<String, Integer> typeCounts = new HashMap<>();
        for (String typeKey : typeMap.keySet()) {
            Activities queryActivity = new Activities();
            queryActivity.setActivityType(typeKey);
            List<Activities> typeActivities = activitiesMapper.selectActivityList(queryActivity);
            int count = typeActivities.size();
            typeCounts.put(typeKey, count);
            totalActivities += count;
        }
        
        // 计算百分比
        for (String typeKey : typeMap.keySet()) {
            Map<String, Object> typeItem = new HashMap<>();
            int count = typeCounts.get(typeKey);
            double percentage = totalActivities > 0 ? (double) count / totalActivities * 100 : 0;
            
            typeItem.put("key", typeKey);
            typeItem.put("name", typeMap.get(typeKey));
            typeItem.put("count", count);
            typeItem.put("percentage", Math.round(percentage));
            
            typeDistribution.add(typeItem);
        }
        
        result.put("typeDistribution", typeDistribution);
        return result;
    }

    @Override
    public Map<String, Object> getRecentActivities(int limit) {
        Map<String, Object> result = new HashMap<>();
        
        // 获取最近的活动
        Activities queryActivity = new Activities();
        List<Activities> allActivities = activitiesMapper.selectActivityList(queryActivity);
        
        // 按创建时间排序，取最新的几个
        allActivities.sort((a, b) -> {
            if (a.getCreatedAt() == null && b.getCreatedAt() == null) return 0;
            if (a.getCreatedAt() == null) return 1;
            if (b.getCreatedAt() == null) return -1;
            return b.getCreatedAt().compareTo(a.getCreatedAt());
        });
        
        List<Map<String, Object>> recentActivities = new ArrayList<>();
        int count = Math.min(limit, allActivities.size());
        
        for (int i = 0; i < count; i++) {
            Activities activity = allActivities.get(i);
            Map<String, Object> activityItem = new HashMap<>();
            
            activityItem.put("id", activity.getActivityId());
            activityItem.put("name", activity.getActivityName());
            activityItem.put("college", activity.getOrganizer());
            activityItem.put("type", getActivityTypeName(activity.getActivityType()));
            activityItem.put("date", activity.getCreatedAt());
            activityItem.put("startTime", activity.getStartTime());
            activityItem.put("endTime", activity.getEndTime());
            
            // 判断活动状态
            String status = getActivityStatus(activity);
            activityItem.put("status", status);
            
            recentActivities.add(activityItem);
        }
        
        result.put("recentActivities", recentActivities);
        return result;
    }

    @Override
    public Map<String, Object> getActivityStatsByCollege(String college) {
        Map<String, Object> result = new HashMap<>();
        
        // 如果选择全部书院，返回总体统计
        if ("all".equals(college)) {
            return getActivityOverview();
        }
        
        // 查询指定书院的活动
        Activities queryActivity = new Activities();
        queryActivity.setOrganizer(college);
        List<Activities> collegeActivities = activitiesMapper.selectActivityList(queryActivity);
        
        int totalActivities = collegeActivities.size();
        
        // 计算进行中的活动数、已完成活动数和参与人数
        Date now = new Date();
        int ongoingActivities = 0;
        int completedActivities = 0;
        int totalParticipants = 0;
        
        for (Activities activity : collegeActivities) {
            if (activity.getActivityStart() != null && activity.getEndTime() != null) {
                if (now.after(activity.getActivityStart()) && now.before(activity.getEndTime())) {
                    ongoingActivities++;
                } else if (now.after(activity.getEndTime())) {
                    completedActivities++;
                }
            }
            // 累加参与人数（activityCapacity = 已选人数）
            totalParticipants += (activity.getActivityCapacity() != null ? activity.getActivityCapacity() : 0);
        }
        
        result.put("totalActivities", totalActivities);
        result.put("ongoingActivities", ongoingActivities);
        result.put("completedActivities", completedActivities);
        result.put("totalParticipants", totalParticipants);
        
        // 获取该书院的活动类型分布
        Map<String, Object> typeDistribution = getActivityTypeDistributionByCollege(college, collegeActivities);
        result.put("typeDistribution", typeDistribution.get("typeDistribution"));
        
        // 获取该书院的最近活动
        Map<String, Object> recentActivities = getRecentActivitiesByCollege(college, collegeActivities, 10);
        result.put("recentActivities", recentActivities.get("recentActivities"));
        
        return result;
    }

    /**
     * 获取活动类型名称
     */
    private String getActivityTypeName(String typeKey) {
        Map<String, String> typeMap = new HashMap<>();
        typeMap.put("1", "人格塑造与价值引领活动类");
        typeMap.put("2", "知识融合与思维进阶活动类");
        typeMap.put("3", "能力锻造与实践创新活动类");
        typeMap.put("4", "社会责任与领军意识活动类");
        return typeMap.getOrDefault(typeKey, "未知类型");
    }

    /**
     * 获取活动状态
     */
    private String getActivityStatus(Activities activity) {
        Date now = new Date();
        
        if (activity.getActivityStart() == null || activity.getEndTime() == null) {
            return "not-started";
        }
        
        if (now.before(activity.getActivityStart())) {
            return "not-started";
        } else if (now.after(activity.getActivityStart()) && now.before(activity.getEndTime())) {
            return "ongoing";
        } else {
            return "completed";
        }
    }

    /**
     * 获取指定书院的活动类型分布
     */
    private Map<String, Object> getActivityTypeDistributionByCollege(String college, List<Activities> collegeActivities) {
        Map<String, Object> result = new HashMap<>();
        
        // 活动类型映射
        Map<String, String> typeMap = new HashMap<>();
        typeMap.put("1", "人格塑造与价值引领活动类");
        typeMap.put("2", "知识融合与思维进阶活动类");
        typeMap.put("3", "能力锻造与实践创新活动类");
        typeMap.put("4", "社会责任与领军意识活动类");
        
        List<Map<String, Object>> typeDistribution = new ArrayList<>();
        int totalActivities = collegeActivities.size();
        
        // 统计各类型活动数量
        Map<String, Integer> typeCounts = new HashMap<>();
        for (String typeKey : typeMap.keySet()) {
            int count = 0;
            for (Activities activity : collegeActivities) {
                if (typeKey.equals(activity.getActivityType())) {
                    count++;
                }
            }
            typeCounts.put(typeKey, count);
        }
        
        // 计算百分比
        for (String typeKey : typeMap.keySet()) {
            Map<String, Object> typeItem = new HashMap<>();
            int count = typeCounts.get(typeKey);
            double percentage = totalActivities > 0 ? (double) count / totalActivities * 100 : 0;
            
            typeItem.put("key", typeKey);
            typeItem.put("name", typeMap.get(typeKey));
            typeItem.put("count", count);
            typeItem.put("percentage", Math.round(percentage));
            
            typeDistribution.add(typeItem);
        }
        
        result.put("typeDistribution", typeDistribution);
        return result;
    }

    /**
     * 获取指定书院的最近活动
     */
    private Map<String, Object> getRecentActivitiesByCollege(String college, List<Activities> collegeActivities, int limit) {
        Map<String, Object> result = new HashMap<>();
        
        // 按创建时间排序，取最新的几个
        collegeActivities.sort((a, b) -> {
            if (a.getCreatedAt() == null && b.getCreatedAt() == null) return 0;
            if (a.getCreatedAt() == null) return 1;
            if (b.getCreatedAt() == null) return -1;
            return b.getCreatedAt().compareTo(a.getCreatedAt());
        });
        
        List<Map<String, Object>> recentActivities = new ArrayList<>();
        int count = Math.min(limit, collegeActivities.size());
        
        for (int i = 0; i < count; i++) {
            Activities activity = collegeActivities.get(i);
            Map<String, Object> activityItem = new HashMap<>();
            
            activityItem.put("id", activity.getActivityId());
            activityItem.put("name", activity.getActivityName());
            activityItem.put("college", activity.getOrganizer());
            activityItem.put("type", getActivityTypeName(activity.getActivityType()));
            activityItem.put("date", activity.getCreatedAt());
            activityItem.put("startTime", activity.getStartTime());
            activityItem.put("endTime", activity.getEndTime());
            
            // 判断活动状态
            String status = getActivityStatus(activity);
            activityItem.put("status", status);
            
            recentActivities.add(activityItem);
        }
        
        result.put("recentActivities", recentActivities);
        return result;
    }

    @Override
    public Map<String, Object> getCollegeActivities(String college) {
        Map<String, Object> result = new HashMap<>();
        
        // 查询指定书院的活动
        Activities queryActivity = new Activities();
        queryActivity.setOrganizer(college);
        List<Activities> collegeActivities = activitiesMapper.selectActivityList(queryActivity);
        
        // 转换为前端需要的格式
        List<Map<String, Object>> activitiesList = new ArrayList<>();
        
        for (Activities activity : collegeActivities) {
            Map<String, Object> activityItem = new HashMap<>();
            
            activityItem.put("id", activity.getActivityId());
            activityItem.put("name", activity.getActivityName());
            activityItem.put("type", getActivityTypeName(activity.getActivityType()));
            activityItem.put("startTime", activity.getStartTime());
            activityItem.put("endTime", activity.getEndTime());
            activityItem.put("location", activity.getActivityLocation());
            activityItem.put("capacity", activity.getActivityTotalCapacity());
            activityItem.put("participants", activity.getActivityCapacity() != null ? activity.getActivityCapacity() : 0);
            activityItem.put("status", getActivityStatus(activity));
            activityItem.put("description", activity.getActivityDescription());
            activityItem.put("organizer", activity.getOrganizer());
            activityItem.put("createdAt", activity.getCreatedAt());
            
            activitiesList.add(activityItem);
        }
        
        // 按创建时间排序（最新的在前）
        activitiesList.sort((a, b) -> {
            Date dateA = (Date) a.get("createdAt");
            Date dateB = (Date) b.get("createdAt");
            if (dateA == null && dateB == null) return 0;
            if (dateA == null) return 1;
            if (dateB == null) return -1;
            return dateB.compareTo(dateA);
        });
        
        result.put("activities", activitiesList);
        result.put("total", activitiesList.size());
        
        return result;
    }

}
