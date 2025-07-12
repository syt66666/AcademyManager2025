package com.ruoyi.system.service.impl;

import com.ruoyi.system.domain.Bookings;
import com.ruoyi.system.mapper.BookingsMapper;
import com.ruoyi.system.service.IBookingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookingsServiceImpl implements IBookingsService {

    @Autowired
    private BookingsMapper bookingsMapper;

    @Override
    public List<Bookings> selectBookingsList(Bookings bookings) {
        return bookingsMapper.selectBookingsList(bookings);
    }

    @Override
    public Bookings selectBookingsById(Long bookingId) {
        return bookingsMapper.selectBookingsById(bookingId);
    }

    @Override
    public int insertBookings(Bookings bookings) {
        return bookingsMapper.insertBookings(bookings);
    }

    @Override
    public int updateBookings(Bookings bookings) {
        return bookingsMapper.updateBookings(bookings);
    }

    @Override
    public int deleteBookingsByIds(Long[] bookingIds) {
        return bookingsMapper.deleteBookingsByIds(bookingIds);
    }
}