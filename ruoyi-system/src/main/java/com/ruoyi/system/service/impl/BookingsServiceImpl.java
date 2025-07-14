package com.ruoyi.system.service.impl;

import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.ServletUtils;
import com.ruoyi.common.utils.ip.IpUtils;
import com.ruoyi.system.domain.AuditHistory;
import com.ruoyi.system.domain.Bookings;
import com.ruoyi.system.domain.StuAbilityScore;
import com.ruoyi.system.domain.StuActivityRecord;
import com.ruoyi.system.domain.dto.BookingDTO;
import com.ruoyi.system.mapper.AuditHistoryMapper;
import com.ruoyi.system.mapper.BookingsMapper;
import com.ruoyi.system.service.IBookingsService;
import com.ruoyi.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * 预约服务实现类
 *
 * 实现了IBookingsService接口，负责预约业务的具体处理逻辑，
 * 并调用BookingsMapper进行数据库操作。
 */
@Service
public class BookingsServiceImpl implements IBookingsService {

    @Autowired
    private BookingsMapper bookingsMapper;
    @Autowired
    private AuditHistoryMapper auditHistoryMapper;
    @Autowired
    private ISysUserService userService;
    /**
     * 查询预约列表
     * @param bookings 查询条件
     * @return 预约列表
     */
    @Override
    public List<Bookings> selectBookingsList(Bookings bookings) {
        return bookingsMapper.selectBookingsList(bookings);
    }
    @Override
    public List<BookingDTO> selectBookingsList2(BookingDTO bookings) {
        return bookingsMapper.selectBookingsList2(bookings);
    }
    public List<BookingDTO> selectBookingsList3(BookingDTO bookings) {
        return bookingsMapper.selectBookingsList3(bookings);
    }
    /**
     * 根据预约ID查询预约详情
     * @param bookingId 预约ID
     * @return 预约对象
     */
    @Override
    public Bookings selectBookingsById(Long bookingId) {
        return bookingsMapper.selectBookingsById(bookingId);
    }

    /**
     * 新增预约
     * @param bookings 预约对象
     * @return 插入结果
     */
    @Override
    public int insertBookings(Bookings bookings) {
        return bookingsMapper.insertBookings(bookings);
    }

    /**
     * 修改预约
     * @param bookings 预约对象
     * @return 修改结果
     */
    @Override
    public int updateBookings(Bookings bookings) {
        if (Objects.equals(bookings.getStatus(), "已通过")|| Objects.equals(bookings.getStatus(), "未通过")) {
            // 1. 获取原始状态
            Bookings originalRecord = bookingsMapper
                    .selectBookingsById(bookings.getBookingId());
            String beforeStatus = originalRecord.getStatus();
            // 2. 执行审核状态更新
            int updateResult = bookingsMapper.updateBookings(bookings);
            if (updateResult <= 0) {
                throw new ServiceException("审核状态更新失败");
            }

            // 3. 保存审核记录
            saveAuditHistory(
                    Math.toIntExact(bookings.getBookingId()),
                    beforeStatus,
                    bookings.getStatus(),
                    bookings.getReviewComment()
            );
            return updateResult;
        }
        return bookingsMapper.updateBookings(bookings);
    }
    // 新增方法：保存审核历史
    private void saveAuditHistory(Integer bookingsId, String beforeStatus,
                                  String afterStatus, String remark) {
        AuditHistory history = new AuditHistory();

        // 填充模块信息
        history.setModuleType(5);  // 1代表文体活动模块
        history.setModuleId(bookingsId.longValue());

        // 状态信息
        history.setAuditStatusBefore(beforeStatus);
        history.setAuditStatusAfter(afterStatus);

        // 操作信息
        history.setAuditAction(getAuditAction(beforeStatus, afterStatus));
        history.setAuditRemark(remark);

        // 用户信息
        SysUser currentUser = userService.selectUserById(SecurityUtils.getUserId());
        history.setAuditorId(currentUser.getUserName());

        // 系统信息
        history.setAuditTime(new Date());
        history.setIpAddress(IpUtils.getIpAddr());
        history.setDeviceInfo(ServletUtils.getRequest().getHeader("User-Agent"));

        // 插入记录
        if (auditHistoryMapper.insertAuditHistory(history) <= 0) {
            throw new ServiceException("审核记录保存失败");
        }
    }
    // 辅助方法：判断审核动作
    private String getAuditAction(String beforeStatus, String afterStatus) {
        if ("已通过".equals(afterStatus)) {
            return "审核通过";
        } else if ("未通过".equals(afterStatus)) {
            return "审核拒绝";
        }
        return "状态变更";
    }
    /**
     * 批量删除预约
     * @param bookingIds 需要删除的预约ID数组
     * @return 删除结果
     */
    @Override
    public int deleteBookingsByIds(Long[] bookingIds) {
        return bookingsMapper.deleteBookingsByIds(bookingIds);
    }


    @Override
    public int deleteBookingsByActivityAndStudent(Long activityId, String studentId) {
        return bookingsMapper.deleteBookingsByActivityAndStudent(activityId, studentId);
    }
    public Map<String, Integer> countAuditStatus() {
        try {
            return bookingsMapper.countAuditStatus();
        } catch (Exception e) {
            return new HashMap<String, Integer>() {{
                put("pending", 0);
                put("approved", 0);
                put("rejected", 0);
            }};
        }
    }
}