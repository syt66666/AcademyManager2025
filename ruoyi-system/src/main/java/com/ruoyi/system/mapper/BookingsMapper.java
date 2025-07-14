package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.Bookings;
import com.ruoyi.system.domain.dto.BookingDTO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

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
     * 批量删除预约
     * @param bookingIds 需要删除的预约ID数组
     * @return 删除结果
     */
    int deleteBookingsByIds(Long[] bookingIds);

    List<BookingDTO> selectBookingsList2(BookingDTO bookings);

    List<BookingDTO> selectBookingsList3(BookingDTO bookings);

    Map<String, Integer> countAuditStatus();
}