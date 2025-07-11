package com.ruoyi.system.service;

import com.ruoyi.system.domain.Activities;
import java.util.List;

public interface IActivitiesService {
    List<Activities> selectActivitiesList(Activities activities);
    Activities selectActivitiesById(Long activityId);
    int insertActivities(Activities activities);
    int updateActivities(Activities activities);
    int deleteActivitiesByIds(Long[] activityIds);
    String importActivities(List<Activities> activityList, boolean updateSupport, String operName);
}
