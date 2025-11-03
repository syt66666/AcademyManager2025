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
     * 获取导师信息详细信息（通过ID主键）
     */
    // @PreAuthorize("@ss.hasPermi('system:tutors:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(tutorsService.selectTutorsById(id));
    }

    /**
     * 获取导师信息详细信息（通过工号）
     */
    // @PreAuthorize("@ss.hasPermi('system:tutors:query')")
    @GetMapping(value = "/tutorId/{tutorId}")
    public AjaxResult getInfoByTutorId(@PathVariable("tutorId") String tutorId)
    {
        List<Tutors> tutors = tutorsService.selectTutorsByTutorId(tutorId);
        // 如果只有一条记录，返回单条；如果有多条，返回列表
        if (tutors != null && tutors.size() == 1) {
            return success(tutors.get(0));
        }
        return success(tutors);
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
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(tutorsService.deleteTutorsByIds(ids));
    }
}
