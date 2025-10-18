package com.ruoyi.system.controller;

import java.util.Collections;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.CourseBookings;
import com.ruoyi.system.domain.CourseBookingDTO;
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
     * 查询课程选课记录列表（包含课程信息）
     */
    @GetMapping("/listWithCourse")
    public TableDataInfo listWithCourse(CourseBookings courseBookings)
    {
        startPage();
        List<CourseBookingDTO> list = courseBookingsService.selectCourseBookingsWithCourseList(courseBookings);
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

    /**
     * 检查学生是否选课了指定课程
     */
    @GetMapping("/checkBooking")
    public AjaxResult checkBookingSimple(@RequestParam Long courseId,
                                         @RequestParam String studentId) {
        boolean isBooked = courseBookingsService.checkIfBooked(courseId, studentId);
        return AjaxResult.success(Collections.singletonMap("isBooked", isBooked));
    }

    /**
     * 根据课程ID和学生ID删除选课记录
     */
    @DeleteMapping("/byCourseAndStudent")
    public AjaxResult deleteByCourseAndStudent(@RequestParam Long courseId,
                                               @RequestParam String studentId) {
        int result = courseBookingsService.deleteByCourseAndStudent(courseId, studentId);
        return result > 0 ? AjaxResult.success("删除成功") : AjaxResult.error("删除失败");
    }

    /**
     * 查询课程选课记录审核列表
     */
    @GetMapping("/auditList")
    public TableDataInfo auditList(CourseBookingDTO courseBookingDTO)
    {
        startPage();
        List<CourseBookingDTO> list = courseBookingsService.selectCourseBookingsListAudit(courseBookingDTO);
        return getDataTable(list);
    }

    /**
     * 查询课程选课记录列表（包含详情）
     */
    @GetMapping("/listWithDetails")
    public TableDataInfo listWithDetails(CourseBookingDTO courseBookingDTO)
    {
        startPage();
        List<CourseBookingDTO> list = courseBookingsService.selectCourseBookingsListWithDetails(courseBookingDTO);
        return getDataTable(list);
    }

    /**
     * 获取课程选课记录详细信息（包含课程和学生信息）
     */
    @GetMapping(value = "/details/{bookingId}")
    public AjaxResult getDetails(@PathVariable("bookingId") Long bookingId)
    {
        return success(courseBookingsService.selectCourseBookingsByBookingIdWithDetails(bookingId));
    }

    /**
     * 审核课程选课记录
     */
    @Log(title = "课程选课审核", businessType = BusinessType.UPDATE)
    @PutMapping("/audit")
    public AjaxResult audit(@RequestBody CourseBookings courseBookings)
    {
        return toAjax(courseBookingsService.updateCourseBookingsAudit(courseBookings));
    }

    /**
     * 批量审核课程选课记录
     */
    @Log(title = "课程选课批量审核", businessType = BusinessType.UPDATE)
    @PutMapping("/batchAudit")
    public AjaxResult batchAudit(@RequestBody List<CourseBookings> courseBookingsList)
    {
        int successCount = 0;
        for (CourseBookings courseBookings : courseBookingsList) {
            if (courseBookingsService.updateCourseBookingsAudit(courseBookings) > 0) {
                successCount++;
            }
        }
        return AjaxResult.success("成功审核 " + successCount + " 条记录");
    }

    /**
     * 获取审核统计信息
     */
    @GetMapping("/auditCount")
    public AjaxResult getAuditCount(@RequestParam(required = false) String organizer)
    {
        return success(courseBookingsService.countCourseBookingAuditStatus(organizer));
    }

    /**
     * 导出课程选课审核列表
     */
    @Log(title = "课程选课审核", businessType = BusinessType.EXPORT)
    @PostMapping("/exportAudit")
    public void exportAudit(HttpServletResponse response, CourseBookingDTO courseBookingDTO)
    {
        List<CourseBookingDTO> list = courseBookingsService.selectCourseBookingsListAudit(courseBookingDTO);
        ExcelUtil<CourseBookingDTO> util = new ExcelUtil<CourseBookingDTO>(CourseBookingDTO.class);
        util.exportExcel(response, list, "课程选课审核数据");
    }
}
