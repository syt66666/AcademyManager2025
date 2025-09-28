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
import com.ruoyi.system.domain.dto.BookingExportDTO;
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
        // 检查是否已经报名过该活动
        if (bookings.getActivityId() != null && bookings.getStudentId() != null) {
            boolean alreadyBooked = checkIfBooked(bookings.getActivityId(), bookings.getStudentId());
            if (alreadyBooked) {
                throw new ServiceException("您已经报名过该活动，不能重复报名");
            }
        }

        // 设置默认状态为"未提交"
        if (bookings.getStatus() == null || bookings.getStatus().isEmpty()) {
            bookings.setStatus("未提交");
        }
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

    /**
     * 审核更新预订记录（只更新审核相关字段，不影响文件字段）
     * @param bookings 要更新的预订对象
     * @return 更新结果（影响的行数）
     */
    @Override
    public int updateBookingsAudit(Bookings bookings) {
        if (Objects.equals(bookings.getStatus(), "已通过") || Objects.equals(bookings.getStatus(), "未通过")) {
            // 1. 获取原始状态
            Bookings originalRecord = bookingsMapper.selectBookingsById(bookings.getBookingId());
            String beforeStatus = originalRecord.getStatus();

            // 2. 执行审核状态更新（只更新审核相关字段）
            int updateResult = bookingsMapper.updateBookingsAudit(bookings);
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
        return bookingsMapper.updateBookingsAudit(bookings);
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
        try {
            System.out.println("删除报名记录: activityId=" + activityId + ", studentId=" + studentId);

            // 检查记录是否存在
            int existingCount = bookingsMapper.checkBookingExists(activityId, studentId);
            System.out.println("检查报名记录是否存在: " + existingCount);

            if (existingCount == 0) {
                System.out.println("报名记录不存在，跳过删除");
                return 1; // 返回成功，因为记录不存在相当于删除成功
            }

            // 执行删除
            int result = bookingsMapper.deleteBookingsByActivityAndStudent(activityId, studentId);
            System.out.println("删除结果: " + result);

            if (result > 0) {
                return result;
            } else {
                // 如果删除影响行数为0，可能是记录已经被删除
                System.out.println("删除影响行数为0，可能记录已被删除");
                return 1; // 返回成功，避免前端报错
            }
        } catch (Exception e) {
            System.err.println("删除报名记录异常: " + e.getMessage());
            e.printStackTrace();
            // 如果是唯一约束冲突等异常，说明记录已被删除
            if (e.getMessage().contains("Duplicate entry") ||
                    e.getMessage().contains("约束冲突") ||
                    e.getMessage().contains("doesn't exist")) {
                System.out.println("检测到约束冲突或记录不存在，记录可能已被删除");
                return 1; // 返回成功，避免前端报错
            }
            throw new RuntimeException("删除报名记录失败", e);
        }
    }

    @Override
    public int deleteBookingsByActivityId(Long activityId) {
        try {
            System.out.println("删除活动相关的所有预约记录: activityId=" + activityId);
            int result = bookingsMapper.deleteBookingsByActivityId(activityId);
            System.out.println("删除结果: " + result + " 条记录被删除");
            return result;
        } catch (Exception e) {
            System.err.println("删除活动相关预约记录异常: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("删除活动相关预约记录失败", e);
        }
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

    @Override
    public boolean checkIfBooked(Long activityId, String studentId) {
        return bookingsMapper.checkIfBooked(activityId, studentId);
    }

    /**
     * 根据学号查所有预约的活动
     * @param studentId
     * @return
     */
    @Override
    public List<Long> getBookedActivityIds(String studentId) {
        return bookingsMapper.selectActivityIdsByStudentId(studentId);
    }

    /**
     * 查询预约学生导出数据
     * @param bookingExportDTO 查询条件
     * @return 导出数据列表
     */
    @Override
    public List<BookingExportDTO> selectBookingExportList(BookingExportDTO bookingExportDTO) {
        return bookingsMapper.selectBookingExportList(bookingExportDTO);
    }
}