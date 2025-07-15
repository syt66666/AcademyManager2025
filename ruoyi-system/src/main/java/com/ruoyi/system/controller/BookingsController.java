package com.ruoyi.system.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.domain.Bookings;
import com.ruoyi.system.domain.StuActivityRecord;
import com.ruoyi.system.domain.dto.BookingDTO;
import com.ruoyi.system.service.IBookingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
     * 管理员查询预约列表和对应活动信息不包含未提交的信息
     */
    @GetMapping("/list3")
    public TableDataInfo list3(BookingDTO bookings) {
        startPage();
        List<BookingDTO> list = bookingsService.selectBookingsList3(bookings);
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
    /**
     * 根据活动ID和学生ID删除预约记录
     */
    @Log(title = "预约信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/byActivityAndStudent")
    public AjaxResult removeByActivityAndStudent(@RequestParam Long activityId,
                                                 @RequestParam String studentId) {
        return toAjax(bookingsService.deleteBookingsByActivityAndStudent(activityId, studentId));
    }
    /**
     * 导出学生预约记录列表
     */
    @Log(title = "学生书院活动记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, BookingDTO bookings)
    {
        List<BookingDTO> list = bookingsService.selectBookingsList3(bookings);
        ExcelUtil<BookingDTO> util = new ExcelUtil<BookingDTO>(BookingDTO.class);
        util.exportExcel(response, list, "学生书院活动记录数据");
    }
    @GetMapping("/auditCount")
    public AjaxResult getAuditStatusCount() {
        Map<String, Integer> countMap = bookingsService.countAuditStatus();
        return AjaxResult.success(countMap);
    }

    /**
     * 检查学生是否预约了指定活动
     */
    @GetMapping("/checkBooking")
    public AjaxResult checkBookingSimple(@RequestParam Long activityId,
                                         @RequestParam String studentId) {
        boolean isBooked = bookingsService.checkIfBooked(activityId, studentId);
        return AjaxResult.success(Collections.singletonMap("isBooked", isBooked));
    }

    /**
     * 根据学号返回所有预约的活动
     */
    @GetMapping("/activities/{studentId}")
    public AjaxResult getBookedActivities(@PathVariable String studentId) {
        List<Long> activityIds = bookingsService.getBookedActivityIds(studentId);
        return AjaxResult.success(activityIds);
    }
}