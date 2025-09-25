package com.ruoyi.system.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.CourseBookings;
import com.ruoyi.system.service.ICourseBookingsService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 课程选课记录Controller
 *
 * @author ruoyi
 * @date 2025-09-25
 */
@RestController
@RequestMapping("/system/courseBookings")
public class CourseBookingsController extends BaseController
{
    @Autowired
    private ICourseBookingsService courseBookingsService;

    /**
     * 查询课程选课记录列表
     */
    @GetMapping("/list")
    public TableDataInfo list(CourseBookings courseBookings)
    {
        startPage();
        List<CourseBookings> list = courseBookingsService.selectCourseBookingsList(courseBookings);
        return getDataTable(list);
    }

    /**
     * 导出课程选课记录列表
     */
    @Log(title = "课程选课记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, CourseBookings courseBookings)
    {
        List<CourseBookings> list = courseBookingsService.selectCourseBookingsList(courseBookings);
        ExcelUtil<CourseBookings> util = new ExcelUtil<CourseBookings>(CourseBookings.class);
        util.exportExcel(response, list, "课程选课记录数据");
    }

    /**
     * 获取课程选课记录详细信息
     */
    @GetMapping(value = "/{bookingId}")
    public AjaxResult getInfo(@PathVariable("bookingId") Long bookingId)
    {
        return success(courseBookingsService.selectCourseBookingsByBookingId(bookingId));
    }

    /**
     * 新增课程选课记录
     */
    @Log(title = "课程选课记录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody CourseBookings courseBookings)
    {
        return toAjax(courseBookingsService.insertCourseBookings(courseBookings));
    }

    /**
     * 修改课程选课记录
     */
    @Log(title = "课程选课记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CourseBookings courseBookings)
    {
        return toAjax(courseBookingsService.updateCourseBookings(courseBookings));
    }

    /**
     * 删除课程选课记录
     */
    @Log(title = "课程选课记录", businessType = BusinessType.DELETE)
    @DeleteMapping("/{bookingIds}")
    public AjaxResult remove(@PathVariable Long[] bookingIds)
    {
        return toAjax(courseBookingsService.deleteCourseBookingsByBookingIds(bookingIds));
    }
}
