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
import com.ruoyi.system.domain.Tutors;
import com.ruoyi.system.service.ITutorsService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 导师信息Controller
 *
 * @author ruoyi
 * @date 2025-03-22
 */
@RestController
@RequestMapping("/system/tutors")
public class TutorsController extends BaseController
{
    @Autowired
    private ITutorsService tutorsService;

    /**
     * 查询导师信息列表
     */
    // @PreAuthorize("@ss.hasPermi('system:tutors:list')")
    @GetMapping("/list")
    public TableDataInfo list(Tutors tutors)
    {
        startPage();
        List<Tutors> list = tutorsService.selectTutorsList(tutors);
        return getDataTable(list);
    }

    /**
     * 导出导师信息列表
     */
    // @PreAuthorize("@ss.hasPermi('system:tutors:export')")
    @Log(title = "导师信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Tutors tutors)
    {
        List<Tutors> list = tutorsService.selectTutorsList(tutors);
        ExcelUtil<Tutors> util = new ExcelUtil<Tutors>(Tutors.class);
        util.exportExcel(response, list, "导师信息数据");
    }

    /**
     * 获取导师信息详细信息
     */
    // @PreAuthorize("@ss.hasPermi('system:tutors:query')")
    @GetMapping(value = "/{tutorId}")
    public AjaxResult getInfo(@PathVariable("tutorId") String tutorId)
    {
        return success(tutorsService.selectTutorsByTutorId(tutorId));
    }

    /**
     * 新增导师信息
     */
    @PreAuthorize("@ss.hasPermi('system:tutors:add')")
    @Log(title = "导师信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Tutors tutors)
    {
        return toAjax(tutorsService.insertTutors(tutors));
    }

    /**
     * 修改导师信息
     */
    @PreAuthorize("@ss.hasPermi('system:tutors:edit')")
    @Log(title = "导师信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Tutors tutors)
    {
        return toAjax(tutorsService.updateTutors(tutors));
    }

    /**
     * 删除导师信息
     */
    @PreAuthorize("@ss.hasPermi('system:tutors:remove')")
    @Log(title = "导师信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{tutorIds}")
    public AjaxResult remove(@PathVariable String[] tutorIds)
    {
        return toAjax(tutorsService.deleteTutorsByTutorIds(tutorIds));
    }
}
