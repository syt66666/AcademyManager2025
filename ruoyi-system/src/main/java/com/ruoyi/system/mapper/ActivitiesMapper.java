package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.Activities;
import java.util.List;

/**
 * 活动Mapper接口
 */
public interface ActivitiesMapper {
    /**
     * 查询活动列表
     * @param activities 查询条件
     * @return 活动列表
     */
    List<Activities> selectActivitiesList(Activities activities);

    /**
     * 根据ID查询活动详情
     * @param activityId 活动ID
     * @return 活动
     */
    Activities selectActivitiesById(Long activityId);

    /**
     * 新增活动
     * @param activities 活动实体
     * @return 插入结果
     */
    int insertActivities(Activities activities);

    /**
     * 修改活动
     * @param activities 活动实体
     * @return 修改结果
     */
    int updateActivities(Activities activities);

    /**
     * 批量删除活动
     * @param activityIds 活动ID数组
     * @return 删除结果
     */
    int deleteActivitiesByIds(Long[] activityIds);

    /**
     * 根据唯一性字段查询活动
     * 用于导入判断是否已存在（可根据实际唯一字段修改参数）
     * @param activities 活动实体
     * @return 查询到的活动
     */
    Activities selectActivitiesByUniqueField(Activities activities);
}