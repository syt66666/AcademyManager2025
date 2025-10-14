package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.Notifications;

/**
 * 系统通知公告Service接口
 * 
 * @author ruoyi
 * @date 2025-10-12
 */
public interface INotificationsService 
{
    /**
     * 查询系统通知公告
     * 
     * @param notiId 系统通知公告主键
     * @return 系统通知公告
     */
    public Notifications selectNotificationsByNotiId(Long notiId);

    /**
     * 查询系统通知公告列表
     * 
     * @param notifications 系统通知公告
     * @return 系统通知公告集合
     */
    public List<Notifications> selectNotificationsList(Notifications notifications);

    /**
     * 新增系统通知公告
     * 
     * @param notifications 系统通知公告
     * @return 结果
     */
    public int insertNotifications(Notifications notifications);

    /**
     * 修改系统通知公告
     * 
     * @param notifications 系统通知公告
     * @return 结果
     */
    public int updateNotifications(Notifications notifications);

    /**
     * 批量删除系统通知公告
     * 
     * @param notiIds 需要删除的系统通知公告主键集合
     * @return 结果
     */
    public int deleteNotificationsByNotiIds(Long[] notiIds);

    /**
     * 删除系统通知公告信息
     * 
     * @param notiId 系统通知公告主键
     * @return 结果
     */
    public int deleteNotificationsByNotiId(Long notiId);
}
