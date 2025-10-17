package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.Activities;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ActivitiesMapper {
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
     * 删除活动
     *
     * @param activityId 活动ID
     * @return 结果
     */
    public int deleteActivityById(Integer activityId);

    /**
     * 批量删除活动
     *
     * @param activityIds 需要删除的活动ID
     * @return 结果
     */
    public int deleteActivityByIds(Integer[] activityIds);

    /**
     * 基于实际预约人数更新活动容量
     */
    int updateCapacityByActualBookings(@Param("activityId") Integer activityId, @Param("version") Integer version);
    
    /**
     * 检查活动是否还有剩余容量
     */
    int checkActivityCapacity(@Param("activityId") Integer activityId);
    
    /**
     * 获取活动当前预约人数
     */
    int getCurrentBookingCount(@Param("activityId") Integer activityId);

    int updateActivity2(Activities activity);

    /**
     * 更新为可报名状态
     * @param now 当前时间
     * @return 更新行数
     */
    int updateStatusToAvailable(@Param("now") String now);

    /**
     * 更新为已截止状态
     * @param now 当前时间
     * @return 更新行数
     */
    int updateStatusToClosed(@Param("now") String now);

    /**
     * 更新为进行中状态
     * @param now 当前时间
     * @return 更新行数
     */
    int updateStatusToOngoing(@Param("now") String now);

    /**
     * 更新为已结束状态
     * @param now 当前时间
     * @return 更新行数
     */
    int updateStatusToEnded(@Param("now") String now);

    /**
     * 查询状态不一致的活动
     * @param now 当前时间
     * @return 不一致的活动列表
     */
    List<Activities> selectInconsistentStatus(@Param("now") String now);

    /**
     * 纠正活动状态
     * @param activityId 活动ID
     * @param status 正确状态
     * @return 更新行数
     */
    int correctActivityStatus(@Param("activityId") Long activityId,
                              @Param("status") String status);

    /**
     * 检查活动名称和组织单位的唯一性
     * @param activityName 活动名称
     * @param organizer 组织单位
     * @param activityId 活动ID（编辑时排除自身）
     * @return 重复的活动数量
     */
    int checkActivityUnique(@Param("activityName") String activityName, 
                           @Param("organizer") String organizer,
                           @Param("activityId") Integer activityId);
}
