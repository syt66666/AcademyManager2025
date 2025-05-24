package com.ruoyi.system.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.system.domain.StuEnrollments;
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
import com.ruoyi.system.domain.Enrollments;
import com.ruoyi.system.service.IEnrollmentsService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 学生选课记录Controller
 *
 * @author ruoyi
 * @date 2025-04-21
 */
@RestController
@RequestMapping("/system/enrollments")
public class EnrollmentsController extends BaseController
{
    @Autowired
    private IEnrollmentsService enrollmentsService;

    /**
     * 查询学生选课记录列表
     */
//    @PreAuthorize("@ss.hasPermi('system:enrollments:list')")
    @GetMapping("/list")
    public TableDataInfo list(Enrollments enrollments)
    {
        startPage();
        List<Enrollments> list = enrollmentsService.selectEnrollmentsList(enrollments);
        return getDataTable(list);
    }
    @GetMapping("/list2")
    public TableDataInfo list(StuEnrollments enrollments)
    {
        startPage();
        List<StuEnrollments> list = enrollmentsService.selectStuEnrollmentsList(enrollments);
        return getDataTable(list);
    }
    /**
     * 导出学生选课记录列表
     */
//    @PreAuthorize("@ss.hasPermi('system:enrollments:export')")
    @Log(title = "学生选课记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Enrollments enrollments)
    {
        List<Enrollments> list = enrollmentsService.selectEnrollmentsList(enrollments);
        ExcelUtil<Enrollments> util = new ExcelUtil<Enrollments>(Enrollments.class);
        util.exportExcel(response, list, "学生选课记录数据");
    }

    /**
     * 获取学生选课记录详细信息
     */
//    @PreAuthorize("@ss.hasPermi('system:enrollments:query')")
    @GetMapping(value = "/{enrollmentId}")
    public AjaxResult getInfo(@PathVariable("enrollmentId") Long enrollmentId)
    {
        return success(enrollmentsService.selectEnrollmentsByEnrollmentId(enrollmentId));
    }

    /**
     * 新增学生选课记录
     */
//    @PreAuthorize("@ss.hasPermi('system:enrollments:add')")
    @Log(title = "学生选课记录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Enrollments enrollments)
    {
        return toAjax(enrollmentsService.insertEnrollments(enrollments));
    }

    /**
     * 修改学生选课记录
     */
//    @PreAuthorize("@ss.hasPermi('system:enrollments:edit')")
    @Log(title = "学生选课记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Enrollments enrollments)
    {
        return toAjax(enrollmentsService.updateEnrollments(enrollments));
    }

    /**
     * 删除学生选课记录
     */
//    @PreAuthorize("@ss.hasPermi('system:enrollments:remove')")
    @Log(title = "学生选课记录", businessType = BusinessType.DELETE)
    @DeleteMapping("/{enrollmentId}")
    public AjaxResult remove(@PathVariable Long enrollmentId)
    {
        return toAjax(enrollmentsService.deleteEnrollmentsByEnrollmentId(enrollmentId));
    }
}
