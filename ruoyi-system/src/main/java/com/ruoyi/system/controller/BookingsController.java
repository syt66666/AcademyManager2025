package com.ruoyi.system.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.system.domain.Bookings;
import com.ruoyi.system.domain.dto.BookingDTO;
import com.ruoyi.system.service.IBookingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/system/bookings")
public class BookingsController extends BaseController {

    @Autowired
    private IBookingsService bookingsService;

    /**
     * 查询预约列表
     */
    @GetMapping("/list")
    public TableDataInfo list(Bookings bookings) {
        startPage();
        List<Bookings> list = bookingsService.selectBookingsList(bookings);
        return getDataTable(list);
    }
    /**
     * 查询预约列表和对应活动信息
     */
    @GetMapping("/list2")
    public TableDataInfo list2(BookingDTO bookings) {
        startPage();
        List<BookingDTO> list = bookingsService.selectBookingsList2(bookings);
        return getDataTable(list);
    }
    /**
     * 获取预约详情
     */
    @GetMapping("/{bookingId}")
    public AjaxResult getInfo(@PathVariable Long bookingId) {
        return success(bookingsService.selectBookingsById(bookingId));
    }

    /**
     * 新增预约
     */
    @Log(title = "预约信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Bookings bookings) {
        return toAjax(bookingsService.insertBookings(bookings));
    }

    /**
     * 修改预约
     */
    @Log(title = "预约信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Bookings bookings) {
        return toAjax(bookingsService.updateBookings(bookings));
    }

    /**
     * 删除预约
     */
    @Log(title = "预约信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{bookingIds}")
    public AjaxResult remove(@PathVariable Long[] bookingIds) {
        return toAjax(bookingsService.deleteBookingsByIds(bookingIds));
    }
}