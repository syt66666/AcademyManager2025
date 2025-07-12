package com.ruoyi.system.service;

import com.ruoyi.system.domain.Bookings;
import java.util.List;

public interface IBookingsService {
    List<Bookings> selectBookingsList(Bookings bookings);
    Bookings selectBookingsById(Long bookingId);
    int insertBookings(Bookings bookings);
    int updateBookings(Bookings bookings);
    int deleteBookingsByIds(Long[] bookingIds);
}