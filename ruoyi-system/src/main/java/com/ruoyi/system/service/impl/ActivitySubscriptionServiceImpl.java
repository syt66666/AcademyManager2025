package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.ActivitySubscriptionMapper;
import com.ruoyi.system.domain.ActivitySubscription;
import com.ruoyi.system.service.IActivitySubscriptionService;

/**
 * 用户活动订阅Service业务层处理
 *
 * @author ruoyi
 * @date 2025-07-17
 */
@Service
public class ActivitySubscriptionServiceImpl implements IActivitySubscriptionService
{
    @Autowired
    private ActivitySubscriptionMapper activitySubscriptionMapper;

    /**
     * 查询用户活动订阅
     *
     * @param subscriptionId 用户活动订阅主键
     * @return 用户活动订阅
     */
    @Override
    public ActivitySubscription selectActivitySubscriptionBySubscriptionId(Long subscriptionId)
    {
        return activitySubscriptionMapper.selectActivitySubscriptionBySubscriptionId(subscriptionId);
    }

    /**
     * 查询用户活动订阅列表
     *
     * @param activitySubscription 用户活动订阅
     * @return 用户活动订阅
     */
    @Override
    public List<ActivitySubscription> selectActivitySubscriptionList(ActivitySubscription activitySubscription)
    {
        return activitySubscriptionMapper.selectActivitySubscriptionList(activitySubscription);
    }

    /**
     * 新增用户活动订阅
     *
     * @param activitySubscription 用户活动订阅
     * @return 结果
     */
    @Override
    public int insertActivitySubscription(ActivitySubscription activitySubscription)
    {
        return activitySubscriptionMapper.insertActivitySubscription(activitySubscription);
    }

    /**
     * 修改用户活动订阅
     *
     * @param activitySubscription 用户活动订阅
     * @return 结果
     */
    @Override
    public int updateActivitySubscription(ActivitySubscription activitySubscription)
    {
        return activitySubscriptionMapper.updateActivitySubscription(activitySubscription);
    }

    /**
     * 批量删除用户活动订阅
     *
     * @param subscriptionIds 需要删除的用户活动订阅主键
     * @return 结果
     */
    @Override
    public int deleteActivitySubscriptionBySubscriptionIds(Long[] subscriptionIds)
    {
        return activitySubscriptionMapper.deleteActivitySubscriptionBySubscriptionIds(subscriptionIds);
    }

    /**
     * 删除用户活动订阅信息
     *
     * @param subscriptionId 用户活动订阅主键
     * @return 结果
     */
    @Override
    public int deleteActivitySubscriptionBySubscriptionId(Long subscriptionId)
    {
        return activitySubscriptionMapper.deleteActivitySubscriptionBySubscriptionId(subscriptionId);
    }
}
