package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.Bookings;
import com.ruoyi.system.domain.dto.BookingDTO;
import com.ruoyi.system.domain.dto.BookingExportDTO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 预约表 数据层接口
 *
 * 提供对数据库中 bookings 表的增删改查操作方法定义
 */
@Mapper
public interface BookingsMapper {
    /**
     * 查询预约列表
     * @param bookings 查询条件
     * @return 预约集合
     */
    List<Bookings> selectBookingsList(Bookings bookings);

    /**
     * 根据预约ID查询预约详情
     * @param bookingId 预约ID
     * @return 预约对象
     */
    Bookings selectBookingsById(Long bookingId);

    /**
     * 新增预约
     * @param bookings 预约对象
     * @return 插入结果
     */
    int insertBookings(Bookings bookings);

    /**
     * 修改预约
     * @param bookings 预约对象
     * @return 修改结果
     */
    int updateBookings(Bookings bookings);

    /**
     * 审核更新预约（只更新审核相关字段，不影响文件字段）
     * @param bookings 预约对象
     * @return 修改结果
     */
    int updateBookingsAudit(Bookings bookings);

    /**
     * 批量删除预约
     * @param bookingIds 需要删除的预约ID数组
     * @return 删除结果
     */
    int deleteBookingsByIds(Long[] bookingIds);
    /**
     * 根据活动ID和学生ID删除预约记录
     * @param activityId 活动ID
     * @param studentId 学生ID
     * @return 删除结果
     */
    int deleteBookingsByActivityAndStudent(@Param("activityId") Long activityId,
                                           @Param("studentId") String studentId);

    List<BookingDTO> selectBookingsList2(BookingDTO bookings);

    List<BookingDTO> selectBookingsList3(BookingDTO bookings);

    Map<String, Integer> countAuditStatus();

    /**
     * 检查学生是否预约了指定活动
     * @param activityId 活动ID
     * @param studentId 学生ID
     * @return 是否已预约
     */
    boolean checkIfBooked(@Param("activityId") Long activityId,
                          @Param("studentId") String studentId);

    List<Long> selectActivityIdsByStudentId(@Param("studentId") String studentId);

    /**
     * 查询预约学生导出数据
     * @param bookingExportDTO 查询条件
     * @return 导出数据列表
     */
    List<BookingExportDTO> selectBookingExportList(BookingExportDTO bookingExportDTO);
}