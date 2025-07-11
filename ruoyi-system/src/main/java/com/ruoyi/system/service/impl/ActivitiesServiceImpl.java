package com.ruoyi.system.service.impl;

import com.ruoyi.system.domain.Activities;
import com.ruoyi.system.mapper.ActivitiesMapper;
import com.ruoyi.system.service.IActivitiesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActivitiesServiceImpl implements IActivitiesService {

    @Autowired
    private ActivitiesMapper activitiesMapper;

    /**
     * 查询活动列表
     */
    @Override
    public List<Activities> selectActivitiesList(Activities activities) {
        return activitiesMapper.selectActivitiesList(activities);
    }

    /**
     * 根据ID查询活动详情
     */
    @Override
    public Activities selectActivitiesById(Long activityId) {
        return activitiesMapper.selectActivitiesById(activityId);
    }

    /**
     * 新增活动
     */
    @Override
    public int insertActivities(Activities activities) {
        return activitiesMapper.insertActivities(activities);
    }

    /**
     * 修改活动
     */
    @Override
    public int updateActivities(Activities activities) {
        return activitiesMapper.updateActivities(activities);
    }

    /**
     * 批量删除活动
     */
    @Override
    public int deleteActivitiesByIds(Long[] activityIds) {
        return activitiesMapper.deleteActivitiesByIds(activityIds);
    }

    /**
     * 导入活动数据
     */
    @Override
    public String importActivities(List<Activities> activityList, boolean updateSupport, String operName) {
        if (activityList == null || activityList.isEmpty()) {
            return "导入数据不能为空！";
        }
        int successNum = 0;
        int updateNum = 0;
        StringBuilder failureMsg = new StringBuilder();
        for (Activities activity : activityList) {
            try {
                Activities exist = activitiesMapper.selectActivitiesByUniqueField(activity); // 假设有唯一性字段
                if (exist == null) {
                    activity.setCreateBy(operName);
                    activitiesMapper.insertActivities(activity);
                    successNum++;
                } else if (updateSupport) {
                    activity.setUpdateBy(operName);
                    activitiesMapper.updateActivities(activity);
                    updateNum++;
                }
            } catch (Exception e) {
                failureMsg.append("活动 ").append(activity.toString()).append(" 导入失败：").append(e.getMessage()).append("; ");
            }
        }
        String msg = "成功导入 " + successNum + " 条";
        if (updateSupport) {
            msg += "，更新 " + updateNum + " 条";
        }
        if (failureMsg.length() > 0) {
            msg += "，失败信息：" + failureMsg;
        }
        return msg;
    }
}