package com.ruoyi.system.service.impl;

import java.util.List;
import com.ruoyi.system.domain.Activities;
import com.ruoyi.system.mapper.ActivitiesMapper;
import com.ruoyi.system.service.IActivitiesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 活动信息 服务层实现
 */
@Service
public class ActivitiesServiceImpl implements IActivitiesService {
    @Autowired
    private ActivitiesMapper activitiesMapper;

    /**
     * 查询活动信息
     *
     * @param activityId 活动ID
     * @return 活动信息
     */
    @Override
    public Activities selectActivityById(Integer activityId)
    {
        return activitiesMapper.selectActivityById(activityId);
    }

    /**
     * 查询活动列表
     *
     * @param activity 活动信息
     * @return 活动
     */
    @Override
    public List<Activities> selectActivityList(Activities activity)
    {
        return activitiesMapper.selectActivityList(activity);
    }

    /**
     * 新增活动
     *
     * @param activity 活动信息
     * @return 结果
     */
    @Override
    public int insertActivity(Activities activity)
    {
        return activitiesMapper.insertActivity(activity);
    }

    /**
     * 修改活动
     *
     * @param activity 活动信息
     * @return 结果
     */
    @Override
    public int updateActivity(Activities activity)
    {
        return activitiesMapper.updateActivity(activity);
    }

    /**
     * 批量删除活动
     *
     * @param activityIds 需要删除的活动ID
     * @return 结果
     */
    @Override
    public int deleteActivityByIds(Integer[] activityIds)
    {
        return activitiesMapper.deleteActivityByIds(activityIds);
    }

    /**
     * 删除活动信息
     *
     * @param activityId 活动ID
     * @return 结果
     */
    @Override
    public int deleteActivityById(Integer activityId)
    {
        return activitiesMapper.deleteActivityById(activityId);
    }
}
