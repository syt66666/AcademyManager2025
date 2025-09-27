package com.ruoyi.system.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import com.ruoyi.system.domain.dto.CourseBookingDTO;
import com.ruoyi.system.domain.dto.CourseBookingExportDTO;
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
    public TableDataInfo listWithCourse(CourseBookingDTO courseBookingDTO)
    {
        startPage();
        List<CourseBookingDTO> list = courseBookingsService.selectCourseBookingsListWithDetails(courseBookingDTO);
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
     * 查询课程选课记录列表（已提交状态）
     */
    @GetMapping("/listSubmitted")
    public TableDataInfo listSubmitted(CourseBookingDTO courseBookingDTO)
    {
        startPage();
        List<CourseBookingDTO> list = courseBookingsService.selectCourseBookingsListSubmitted(courseBookingDTO);
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
     * 导出课程选课学生列表
     */
    @Log(title = "课程选课学生", businessType = BusinessType.EXPORT)
    @PostMapping("/exportStudents")
    public void exportStudents(HttpServletResponse response, CourseBookingExportDTO courseBookingExportDTO)
    {
        List<CourseBookingExportDTO> list = courseBookingsService.selectCourseBookingExportList(courseBookingExportDTO);
        ExcelUtil<CourseBookingExportDTO> util = new ExcelUtil<CourseBookingExportDTO>(CourseBookingExportDTO.class);
        util.exportExcel(response, list, "课程选课学生数据");
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
     * 审核课程选课记录
     */
    @Log(title = "课程选课审核", businessType = BusinessType.UPDATE)
    @PutMapping("/audit")
    public AjaxResult audit(@RequestBody CourseBookings courseBookings)
    {
        return toAjax(courseBookingsService.updateCourseBookingsAudit(courseBookings));
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
     * 根据课程ID和学生ID删除选课记录
     */
    @Log(title = "课程选课记录", businessType = BusinessType.DELETE)
    @DeleteMapping("/course/{courseId}/student/{studentId}")
    public AjaxResult removeByCourseAndStudent(@PathVariable Long courseId, @PathVariable String studentId)
    {
        return toAjax(courseBookingsService.deleteCourseBookingsByCourseAndStudent(courseId, studentId));
    }

    /**
     * 检查课程选课记录是否存在
     */
    @GetMapping("/check/{courseId}/{studentId}")
    public AjaxResult checkExists(@PathVariable Long courseId, @PathVariable String studentId)
    {
        Map<String, Object> result = courseBookingsService.checkCourseBookingSimple(courseId, studentId);
        return success(result);
    }

    /**
     * 检查是否已选课
     */
    @GetMapping("/checkBooked/{courseId}/{studentId}")
    public AjaxResult checkBooked(@PathVariable Long courseId, @PathVariable String studentId)
    {
        boolean isBooked = courseBookingsService.checkIfCourseBooked(courseId, studentId);
        Map<String, Object> result = new HashMap<>();
        result.put("isBooked", isBooked);
        return success(result);
    }

    /**
     * 根据学生ID查询已选课程ID列表
     */
    @GetMapping("/courses/{studentId}")
    public AjaxResult getCoursesByStudent(@PathVariable String studentId)
    {
        List<Long> courseIds = courseBookingsService.selectCourseIdsByStudentId(studentId);
        return success(courseIds);
    }

    /**
     * 统计课程选课审核状态
     */
    @GetMapping("/auditStatus")
    public AjaxResult getAuditStatus()
    {
        Map<String, Integer> status = courseBookingsService.countCourseBookingAuditStatus();
        return success(status);
    }
}
