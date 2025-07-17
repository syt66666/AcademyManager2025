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
     * 在活动表中更新容量
     */
    int increaseCapacity(Integer activityId,Integer version);
    int decreaseCapacity(Integer activityId,Integer version);

    int updateActivity2(Activities activity);
}
