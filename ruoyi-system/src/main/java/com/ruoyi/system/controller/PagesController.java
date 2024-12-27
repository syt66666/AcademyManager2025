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
import com.ruoyi.system.domain.Pages;
import com.ruoyi.system.service.IPagesService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 问卷信息Controller
 * 
 * @author ruoyi
 * @date 2024-12-26
 */
@RestController
@RequestMapping("/system/pages")
public class PagesController extends BaseController
{
    @Autowired
    private IPagesService pagesService;

    /**
     * 查询问卷信息列表
     */
    @PreAuthorize("@ss.hasPermi('system:pages:list')")
    @GetMapping("/list")
    public TableDataInfo list(Pages pages)
    {
        startPage();
        List<Pages> list = pagesService.selectPagesList(pages);
        return getDataTable(list);
    }

    /**
     * 导出问卷信息列表
     */
    @PreAuthorize("@ss.hasPermi('system:pages:export')")
    @Log(title = "问卷信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Pages pages)
    {
        List<Pages> list = pagesService.selectPagesList(pages);
        ExcelUtil<Pages> util = new ExcelUtil<Pages>(Pages.class);
        util.exportExcel(response, list, "问卷信息数据");
    }

    /**
     * 获取问卷信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:pages:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(pagesService.selectPagesById(id));
    }

    /**
     * 新增问卷信息
     */
    @PreAuthorize("@ss.hasPermi('system:pages:add')")
    @Log(title = "问卷信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Pages pages)
    {
        return toAjax(pagesService.insertPages(pages));
    }

    /**
     * 修改问卷信息
     */
    @PreAuthorize("@ss.hasPermi('system:pages:edit')")
    @Log(title = "问卷信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Pages pages)
    {
        return toAjax(pagesService.updatePages(pages));
    }

    /**
     * 删除问卷信息
     */
    @PreAuthorize("@ss.hasPermi('system:pages:remove')")
    @Log(title = "问卷信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(pagesService.deletePagesByIds(ids));
    }
}
