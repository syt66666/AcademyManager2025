package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.ActivitySubscription;

/**
 * 用户活动订阅Mapper接口
 *
 * @author ruoyi
 * @date 2025-07-17
 */
public interface ActivitySubscriptionMapper
{
    /**
     * 查询用户活动订阅
     *
     * @param subscriptionId 用户活动订阅主键
     * @return 用户活动订阅
     */
    public ActivitySubscription selectActivitySubscriptionBySubscriptionId(Long subscriptionId);

    /**
     * 查询用户活动订阅列表
     *
     * @param activitySubscription 用户活动订阅
     * @return 用户活动订阅集合
     */
    public List<ActivitySubscription> selectActivitySubscriptionList(ActivitySubscription activitySubscription);

    /**
     * 新增用户活动订阅
     *
     * @param activitySubscription 用户活动订阅
     * @return 结果
     */
    public int insertActivitySubscription(ActivitySubscription activitySubscription);

    /**
     * 修改用户活动订阅
     *
     * @param activitySubscription 用户活动订阅
     * @return 结果
     */
    public int updateActivitySubscription(ActivitySubscription activitySubscription);

    /**
     * 删除用户活动订阅
     *
     * @param subscriptionId 用户活动订阅主键
     * @return 结果
     */
    public int deleteActivitySubscriptionBySubscriptionId(Long subscriptionId);

    /**
     * 批量删除用户活动订阅
     *
     * @param subscriptionIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteActivitySubscriptionBySubscriptionIds(Long[] subscriptionIds);
}
