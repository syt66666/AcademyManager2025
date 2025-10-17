package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.Activities;

public interface IActivitiesService {
    /**
     * 查询活动信息
     *
     * @param activityId 活动ID
     * @return 活动信息
     */
    public Activities selectActivityById(Integer activityId);

    /**
     * 查询活动列表
     *
     * @param activity 活动信息
     * @return 活动集合
     */
    public List<Activities> selectActivityList(Activities activity);

    /**
     * 新增活动
     *
     * @param activity 活动信息
     * @return 结果
     */
    public int insertActivity(Activities activity);

    /**
     * 修改活动
     *
     * @param activity 活动信息
     * @return 结果
     */
    public int updateActivity(Activities activity);

    /**
     * 批量删除活动
     *
     * @param activityIds 需要删除的活动ID
     * @return 结果
     */
    public int deleteActivityByIds(Integer[] activityIds);

    /**
     * 删除活动信息
     *
     * @param activityId 活动ID
     * @return 结果
     */
    public int deleteActivityById(Integer activityId);

    String importActivity(List<Activities> userList, boolean updateSupport, String operName);

    /**
     * 基于实际预约人数更新活动容量
     */
    int updateCapacityByActualBookings(Integer activityId, Integer version);
    
    /**
     * 获取活动当前预约人数
     */
    int getCurrentBookingCount(Integer activityId);
    
    /**
     * 检查活动剩余容量
     */
    int checkActivityCapacity(Integer activityId);

    int updateActivity2(Activities activity);

    /**
     * 检查活动名称和组织单位的唯一性
     *
     * @param activityName 活动名称
     * @param organizer 组织单位
     * @param activityId 活动ID（编辑时排除自身）
     * @return 是否唯一
     */
    boolean checkActivityUnique(String activityName, String organizer, Integer activityId);

    /**
     * 检查活动是否已结束
     *
     * @param activityId 活动ID
     * @return 是否已结束
     */
    boolean isActivityEnded(Integer activityId);
}
