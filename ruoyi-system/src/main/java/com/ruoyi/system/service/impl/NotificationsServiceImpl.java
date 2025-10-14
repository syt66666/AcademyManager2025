package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.NotificationsMapper;
import com.ruoyi.system.domain.Notifications;
import com.ruoyi.system.service.INotificationsService;

/**
 * 系统通知公告Service业务层处理
 *
 * @author ruoyi
 * @date 2025-10-12
 */
@Service
public class NotificationsServiceImpl implements INotificationsService
{
    @Autowired
    private NotificationsMapper notificationsMapper;

    /**
     * 查询系统通知公告
     *
     * @param notiId 系统通知公告主键
     * @return 系统通知公告
     */
    @Override
    public Notifications selectNotificationsByNotiId(Long notiId)
    {
        return notificationsMapper.selectNotificationsByNotiId(notiId);
    }

    /**
     * 查询系统通知公告列表
     *
     * @param notifications 系统通知公告
     * @return 系统通知公告
     */
    @Override
    public List<Notifications> selectNotificationsList(Notifications notifications)
    {
        return notificationsMapper.selectNotificationsList(notifications);
    }

    /**
     * 新增系统通知公告
     *
     * @param notifications 系统通知公告
     * @return 结果
     */
    @Override
    public int insertNotifications(Notifications notifications)
    {
        return notificationsMapper.insertNotifications(notifications);
    }

    /**
     * 修改系统通知公告
     *
     * @param notifications 系统通知公告
     * @return 结果
     */
    @Override
    public int updateNotifications(Notifications notifications)
    {
        return notificationsMapper.updateNotifications(notifications);
    }

    /**
     * 批量删除系统通知公告
     *
     * @param notiIds 需要删除的系统通知公告主键
     * @return 结果
     */
    @Override
    public int deleteNotificationsByNotiIds(Long[] notiIds)
    {
        return notificationsMapper.deleteNotificationsByNotiIds(notiIds);
    }

    /**
     * 删除系统通知公告信息
     *
     * @param notiId 系统通知公告主键
     * @return 结果
     */
    @Override
    public int deleteNotificationsByNotiId(Long notiId)
    {
        return notificationsMapper.deleteNotificationsByNotiId(notiId);
    }
}
