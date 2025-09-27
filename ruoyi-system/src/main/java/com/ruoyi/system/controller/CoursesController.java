package com.ruoyi.system.controller;

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
     * 报名
     *
     * @param params
     * @return
     */
    @PostMapping("/signUpCapacity")
    public AjaxResult signUpCapacity(@RequestBody Map<String, Integer> params) {
        Integer courseId = params.get("courseId");
        Integer version = params.get("version"); // 获取请求体中的 version
        int result = coursesService.decreaseCapacity(courseId,version);
        return result > 0 ? AjaxResult.success("选课成功") : AjaxResult.error("选课失败");
    }

    @PostMapping("/cancelSignUpCapacity")
    public AjaxResult cancelSignUpCapacity(@RequestBody Map<String, Integer> params) {
        Integer courseId = params.get("courseId");
        Integer version = params.get("version");
        int result = coursesService.increaseCapacity(courseId,version);
        return result > 0 ? AjaxResult.success("取消选课成功") : AjaxResult.error("取消选课失败");
    }

    /**
     * 检查活动名称和组织单位的唯一性
     */
    @PostMapping("/checkUnique")
    public AjaxResult checkUnique(@RequestBody Map<String, Object> params) {
        String courseName = (String) params.get("courseName");
        String organizer = (String) params.get("organizer");
        Integer courseId = (Integer) params.get("courseId");

        boolean isUnique = coursesService.checkCourseUnique(courseName, organizer, courseId);
        return AjaxResult.success(isUnique);
    }
}
