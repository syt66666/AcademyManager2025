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
import com.ruoyi.system.domain.TutorStudentRelation;
import com.ruoyi.system.service.ITutorStudentRelationService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 导师学生关联Controller
 *
 * @author ruoyi
 * @date 2025-10-24
 */
@RestController
@RequestMapping("/system/relation")
public class TutorStudentRelationController extends BaseController
{
    @Autowired
    private ITutorStudentRelationService tutorStudentRelationService;

    /**
     * 查询导师学生关联列表
     */
    // @PreAuthorize("@ss.hasPermi('system:relation:list')")
    @GetMapping("/list")
    public TableDataInfo list(TutorStudentRelation tutorStudentRelation)
    {
        startPage();
        List<TutorStudentRelation> list = tutorStudentRelationService.selectTutorStudentRelationList(tutorStudentRelation);
        return getDataTable(list);
    }

    /**
     * 导出导师学生关联列表
     */
    // @PreAuthorize("@ss.hasPermi('system:relation:export')")
    @Log(title = "导师学生关联", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, TutorStudentRelation tutorStudentRelation)
    {
        List<TutorStudentRelation> list = tutorStudentRelationService.selectTutorStudentRelationList(tutorStudentRelation);
        ExcelUtil<TutorStudentRelation> util = new ExcelUtil<TutorStudentRelation>(TutorStudentRelation.class);
        util.exportExcel(response, list, "导师学生关联数据");
    }

    /**
     * 获取导师学生关联详细信息
     */
    // @PreAuthorize("@ss.hasPermi('system:relation:query')")
    @GetMapping(value = "/{relationId}")
    public AjaxResult getInfo(@PathVariable("relationId") Long relationId)
    {
        return success(tutorStudentRelationService.selectTutorStudentRelationByRelationId(relationId));
    }

    /**
     * 新增导师学生关联
     */
    // @PreAuthorize("@ss.hasPermi('system:relation:add')")
    @Log(title = "导师学生关联", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody TutorStudentRelation tutorStudentRelation)
    {
        return toAjax(tutorStudentRelationService.insertTutorStudentRelation(tutorStudentRelation));
    }

    /**
     * 修改导师学生关联
     */
    // @PreAuthorize("@ss.hasPermi('system:relation:edit')")
    @Log(title = "导师学生关联", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody TutorStudentRelation tutorStudentRelation)
    {
        return toAjax(tutorStudentRelationService.updateTutorStudentRelation(tutorStudentRelation));
    }

    /**
     * 删除导师学生关联
     */
    @PreAuthorize("@ss.hasPermi('system:relation:remove')")
    @Log(title = "导师学生关联", businessType = BusinessType.DELETE)
    @DeleteMapping("/{relationIds}")
    public AjaxResult remove(@PathVariable Long[] relationIds)
    {
        return toAjax(tutorStudentRelationService.deleteTutorStudentRelationByRelationIds(relationIds));
    }
}
