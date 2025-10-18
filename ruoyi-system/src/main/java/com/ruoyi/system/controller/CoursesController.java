package com.ruoyi.system.controller;

import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
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
import com.ruoyi.system.domain.Courses;
import com.ruoyi.system.service.ICoursesService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 【请填写功能名称】Controller
 *
 * @author ruoyi
 * @date 2025-09-25
 */
@RestController
@RequestMapping("/system/courses")
public class CoursesController extends BaseController
{
    @Autowired
    private ICoursesService coursesService;

    /**
     * 查询【请填写功能名称】列表
     */
    @GetMapping("/list")
    public TableDataInfo list(Courses courses)
    {
        startPage();
        List<Courses> list = coursesService.selectCoursesList(courses);
        return getDataTable(list);
    }

    /**
     * 导出【请填写功能名称】列表
     */
    @Log(title = "【请填写功能名称】", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Courses courses)
    {
        List<Courses> list = coursesService.selectCoursesList(courses);
        ExcelUtil<Courses> util = new ExcelUtil<Courses>(Courses.class);
        util.exportExcel(response, list, "【请填写功能名称】数据");
    }
    /**
     * 获取【请填写功能名称】详细信息
     */

    @GetMapping(value = "/{courseId}")
    public AjaxResult getInfo(@PathVariable("courseId") Long courseId)
    {
        return success(coursesService.selectCoursesByCourseId(courseId));
    }

    /**
     * 新增【请填写功能名称】
     */
    @Log(title = "【请填写功能名称】", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Courses courses)
    {
        return toAjax(coursesService.insertCourses(courses));
    }

    /**
     * 修改【请填写功能名称】
     */
    @Log(title = "【请填写功能名称】", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Courses courses)
    {
        return toAjax(coursesService.updateCourses(courses));
    }

    /**
     * 删除【请填写功能名称】
     */
    @Log(title = "【请填写功能名称】", businessType = BusinessType.DELETE)
    @DeleteMapping("/{courseIds}")
    public AjaxResult remove(@PathVariable Long[] courseIds)
    {
        return toAjax(coursesService.deleteCoursesByCourseIds(courseIds));
    }

    /**
     * 选课容量变化
     */
    @PostMapping("/signUpCapacity")
    public AjaxResult signUpCapacity(@RequestBody Map<String, Object> params) {
        Long courseId = Long.valueOf(params.get("courseId").toString());
        Integer version = Integer.valueOf(params.get("version").toString());
        int result = coursesService.decreaseCapacity(courseId, version);
        return result > 0 ? AjaxResult.success("选课成功") : AjaxResult.error("选课失败");
    }

    /**
     * 取消选课容量变化
     */
    @PostMapping("/cancelSignUpCapacity")
    public AjaxResult cancelSignUpCapacity(@RequestBody Map<String, Object> params) {
        Long courseId = Long.valueOf(params.get("courseId").toString());
        Integer version = Integer.valueOf(params.get("version").toString());
        int result = coursesService.increaseCapacity(courseId, version);
        return result > 0 ? AjaxResult.success("取消选课成功") : AjaxResult.error("取消选课失败");
    }

    /**
     * 课程选课 - 原子性操作，解决并发问题
     */
    @Log(title = "课程选课", businessType = BusinessType.INSERT)
    @PostMapping("/signUp")
    public AjaxResult signUp(@RequestBody Map<String, Object> params) {
        try {
            Long courseId = Long.valueOf(params.get("courseId").toString());
            String studentId = (String) params.get("studentId");
            Integer version = Integer.valueOf(params.get("version").toString());
            
            // 使用事务性的原子操作
            int result = coursesService.signUpWithTransaction(courseId, studentId, version);
            
            if (result > 0) {
                return AjaxResult.success("选课成功");
            } else {
                return AjaxResult.error("选课失败，可能容量已满或已选课过");
            }
        } catch (Exception e) {
            return AjaxResult.error("选课失败: " + e.getMessage());
        }
    }

    /**
     * 课程取消选课 - 原子性操作，解决并发问题
     */
    @Log(title = "课程取消选课", businessType = BusinessType.DELETE)
    @PostMapping("/cancelSignUp")
    public AjaxResult cancelSignUp(@RequestBody Map<String, Object> params) {
        try {
            Long courseId = Long.valueOf(params.get("courseId").toString());
            String studentId = (String) params.get("studentId");
            Integer version = Integer.valueOf(params.get("version").toString());
            
            // 使用事务性的原子操作
            int result = coursesService.cancelSignUpWithTransaction(courseId, studentId, version);
            
            if (result > 0) {
                return AjaxResult.success("取消选课成功");
            } else {
                return AjaxResult.error("取消选课失败，可能未选课或已取消");
            }
        } catch (Exception e) {
            return AjaxResult.error("取消选课失败: " + e.getMessage());
        }
    }
}
