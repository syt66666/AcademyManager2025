package com.ruoyi.system.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.Courses;
import com.ruoyi.system.service.ICoursesService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 书院选课Controller
 * 
 * @author ruoyi
 * @date 2025-09-22
 */
@RestController
@RequestMapping("/system/courses")
public class CoursesController extends BaseController
{
    @Autowired
    private ICoursesService coursesService;

    /**
     * 查询书院选课列表
     */

    @GetMapping("/list")
    public TableDataInfo list(Courses courses)
    {
        startPage();
        List<Courses> list = coursesService.selectCoursesList(courses);
        return getDataTable(list);
    }

    /**
     * 导出书院选课列表
     */

    @Log(title = "书院选课", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Courses courses)
    {
        List<Courses> list = coursesService.selectCoursesList(courses);
        ExcelUtil<Courses> util = new ExcelUtil<Courses>(Courses.class);
        util.exportExcel(response, list, "书院选课数据");
    }

    /**
     * 获取书院选课详细信息
     */

    @GetMapping(value = "/{courseId}")
    public AjaxResult getInfo(@PathVariable("courseId") Long courseId)
    {
        return success(coursesService.selectCoursesByCourseId(courseId));
    }

    /**
     * 新增书院选课
     */

    @Log(title = "书院选课", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Courses courses)
    {
        return toAjax(coursesService.insertCourses(courses));
    }

    /**
     * 修改书院选课
     */

    @Log(title = "书院选课", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Courses courses)
    {
        return toAjax(coursesService.updateCourses(courses));
    }

    /**
     * 删除书院选课
     */

    @Log(title = "书院选课", businessType = BusinessType.DELETE)
	@DeleteMapping("/{courseIds}")
    public AjaxResult remove(@PathVariable Long[] courseIds)
    {
        return toAjax(coursesService.deleteCoursesByCourseIds(courseIds));
    }
    /**
     * 学生选课
     */
    @Log(title = "书院选课", businessType = BusinessType.UPDATE)
    @PostMapping("/signUp")
    public AjaxResult signUp(@RequestBody SignUpRequest request)
    {
        try {
            // 调用选课服务
            int result = coursesService.signUpCourse(request.getCourseId(), request.getStudentId());
            if (result > 0) {
                return success("选课成功");
            } else {
                return error("选课失败，课程可能已满或您已选过该课程");
            }
        } catch (Exception e) {
            return error(e.getMessage());
        }
    }

    /**
     * 学生取消选课
     */
    @Log(title = "书院选课", businessType = BusinessType.UPDATE)
    @PostMapping("/cancelSignUp")
    public AjaxResult cancelSignUp(@RequestBody SignUpRequest request)
    {
        try {
            int result = coursesService.cancelSignUp(request.getCourseId(), request.getStudentId());
            if (result > 0) {
                return success("取消选课成功");
            } else {
                return error("取消选课失败，您可能未选该课程");
            }
        } catch (Exception e) {
            return error(e.getMessage());
        }
    }

    /**
     * 检查选课状态
     */
    @GetMapping("/checkBooking")
    public AjaxResult checkBooking(@RequestParam Long courseId, @RequestParam String studentId)
    {
        try {
            boolean isBooked = coursesService.checkBookingStatus(courseId, studentId);
            return success(isBooked ? "已选课" : "未选课");
        } catch (Exception e) {
            return error("检查选课状态失败");
        }
    }

    // 请求参数类
    public static class SignUpRequest {
        private Long courseId;
        private String studentId;
        private Integer version;

        // getter 和 setter 方法
        public Long getCourseId() { return courseId; }
        public void setCourseId(Long courseId) { this.courseId = courseId; }

        public String getStudentId() { return studentId; }
        public void setStudentId(String studentId) { this.studentId = studentId; }

        public Integer getVersion() { return version; }
        public void setVersion(Integer version) { this.version = version; }
    }

}
