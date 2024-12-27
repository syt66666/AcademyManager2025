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
import com.ruoyi.system.domain.Questions;
import com.ruoyi.system.service.IQuestionsService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 试题管理Controller
 * 
 * @author ruoyi
 * @date 2024-12-27
 */
@RestController
@RequestMapping("/system/questions")
public class QuestionsController extends BaseController
{
    @Autowired
    private IQuestionsService questionsService;

    /**
     * 查询试题管理列表
     */
    @PreAuthorize("@ss.hasPermi('system:questions:list')")
    @GetMapping("/list")
    public TableDataInfo list(Questions questions)
    {
        startPage();
        List<Questions> list = questionsService.selectQuestionsList(questions);
        return getDataTable(list);
    }

    /**
     * 导出试题管理列表
     */
    @PreAuthorize("@ss.hasPermi('system:questions:export')")
    @Log(title = "试题管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Questions questions)
    {
        List<Questions> list = questionsService.selectQuestionsList(questions);
        ExcelUtil<Questions> util = new ExcelUtil<Questions>(Questions.class);
        util.exportExcel(response, list, "试题管理数据");
    }

    /**
     * 获取试题管理详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:questions:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(questionsService.selectQuestionsById(id));
    }

    /**
     * 新增试题管理
     */
    @PreAuthorize("@ss.hasPermi('system:questions:add')")
    @Log(title = "试题管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Questions questions)
    {
        return toAjax(questionsService.insertQuestions(questions));
    }

    /**
     * 修改试题管理
     */
    @PreAuthorize("@ss.hasPermi('system:questions:edit')")
    @Log(title = "试题管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Questions questions)
    {
        return toAjax(questionsService.updateQuestions(questions));
    }

    /**
     * 删除试题管理
     */
    @PreAuthorize("@ss.hasPermi('system:questions:remove')")
    @Log(title = "试题管理", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(questionsService.deleteQuestionsByIds(ids));
    }
}
