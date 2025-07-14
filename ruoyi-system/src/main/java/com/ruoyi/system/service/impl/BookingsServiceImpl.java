package com.ruoyi.system.service.impl;

import com.ruoyi.system.domain.Bookings;
import com.ruoyi.system.domain.dto.BookingDTO;
import com.ruoyi.system.mapper.BookingsMapper;
import com.ruoyi.system.service.IBookingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
        return bookingsMapper.updateBookings(bookings);
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
}