package com.ruoyi.system.service;

import com.ruoyi.system.domain.Bookings;
import com.ruoyi.system.domain.dto.BookingDTO;
import com.ruoyi.system.domain.dto.BookingExportDTO;

import java.util.List;
import java.util.Map;

public interface IBookingsService {

    /**
     * 查询预订列表
     * @param bookings 查询条件（可以封装查询字段）
     * @return 符合条件的预订列表
     */
    List<Bookings> selectBookingsList(Bookings bookings);

    /**
     * 根据预订ID查询详细信息
     * @param bookingId 预订ID
     * @return 对应的 Bookings 对象
     */
    Bookings selectBookingsById(Long bookingId);

    /**
     * 新增预订记录
     * @param bookings 要插入的预订对象
     * @return 插入结果（影响的行数，通常是1）
     */
    int insertBookings(Bookings bookings);

    /**
     * 修改预订记录
     * @param bookings 要更新的预订对象
     * @return 更新结果（影响的行数）
     */
    int updateBookings(Bookings bookings);

    /**
     * 审核更新预订记录（只更新审核相关字段，不影响文件字段）
     * @param bookings 要更新的预订对象
     * @return 更新结果（影响的行数）
     */
    int updateBookingsAudit(Bookings bookings);

    /**
     * 批量删除预订记录
     * @param bookingIds 要删除的预订ID数组
     * @return 删除结果（影响的行数）
     */
    int deleteBookingsByIds(Long[] bookingIds);

    /**
     * 根据活动ID和学生ID删除预约记录
     * @param activityId 活动ID
     * @param studentId 学生ID
     * @return 删除结果（影响的行数）
     */
    int deleteBookingsByActivityAndStudent(Long activityId, String studentId);

    List<BookingDTO> selectBookingsList2(BookingDTO bookings);
    List<BookingDTO> selectBookingsList3(BookingDTO bookings);

    Map<String, Integer> countAuditStatus(String organizer);

    boolean checkIfBooked(Long activityId, String studentId);

    /**
     * 批量导入指定活动的学生预约名单
     * @param activityId 活动ID
     * @param status 导入的审核状态（未提交/已通过）
     * @param studentIds 学号列表
     * @return 结果统计：inserted、skippedNotFound、skippedDuplicate
     */
    Map<String, Integer> importBookingsStudents(Long activityId, String status, List<String> studentIds);

    List<Long> getBookedActivityIds(String studentId);

    /**
     * 查询预约学生导出数据
     * @param bookingExportDTO 查询条件
     * @return 导出数据列表
     */
    List<BookingExportDTO> selectBookingExportList(BookingExportDTO bookingExportDTO);

    /**
     * 根据活动ID删除所有相关的预约记录
     * @param activityId 活动ID
     * @return 删除结果（影响的行数）
     */
    int deleteBookingsByActivityId(Long activityId);

    /**
     * 报名后更新活动容量
     * @param activityId 活动ID
     * @param version 版本号
     * @return 更新结果
     */
    int updateActivityCapacityAfterBooking(Integer activityId, Integer version);

    /**
     * 事务性报名操作 - 解决并发问题
     * @param activityId 活动ID
     * @param studentId 学生ID
     * @param version 版本号
     * @return 操作结果
     */
    int signUpWithTransaction(Long activityId, String studentId, Integer version);

    /**
     * 事务性取消报名操作 - 解决并发问题
     * @param activityId 活动ID
     * @param studentId 学生ID
     * @param version 版本号
     * @return 操作结果
     */
    int cancelSignUpWithTransaction(Long activityId, String studentId, Integer version);
}
