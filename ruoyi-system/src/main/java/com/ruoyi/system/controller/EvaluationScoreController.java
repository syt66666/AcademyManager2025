package com.ruoyi.system.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
// import org.springframework.security.access.prepost.PreAuthorize;
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
import com.ruoyi.system.domain.EvaluationScore;
import com.ruoyi.system.service.IEvaluationScoreService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 问卷得分Controller
 * 
 * @author ruoyi
 * @date 2025-10-21
 */
@RestController
@RequestMapping("/questionnaire/score")
public class EvaluationScoreController extends BaseController
{
    @Autowired
    private IEvaluationScoreService evaluationScoreService;

    /**
     * 查询问卷得分列表
     */
    // @PreAuthorize("@ss.hasPermi('system:score:list')")
    @GetMapping("/list")
    public TableDataInfo list(EvaluationScore evaluationScore)
    {
        startPage();
        List<EvaluationScore> list = evaluationScoreService.selectEvaluationScoreList(evaluationScore);
        return getDataTable(list);
    }

    /**
     * 导出问卷得分列表
     */
    // @PreAuthorize("@ss.hasPermi('system:score:export')")
    @Log(title = "问卷得分", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, EvaluationScore evaluationScore)
    {
        List<EvaluationScore> list = evaluationScoreService.selectEvaluationScoreList(evaluationScore);
        ExcelUtil<EvaluationScore> util = new ExcelUtil<EvaluationScore>(EvaluationScore.class);
        util.exportExcel(response, list, "问卷得分数据");
    }

    /**
     * 获取问卷得分详细信息
     */
    // @PreAuthorize("@ss.hasPermi('system:score:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(evaluationScoreService.selectEvaluationScoreById(id));
    }

    /**
     * 新增问卷得分
     */
    // @PreAuthorize("@ss.hasPermi('system:score:add')")
    @Log(title = "问卷得分", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody EvaluationScore evaluationScore)
    {
        return toAjax(evaluationScoreService.insertEvaluationScore(evaluationScore));
    }

    /**
     * 修改问卷得分
     */
    // @PreAuthorize("@ss.hasPermi('system:score:edit')")
    @Log(title = "问卷得分", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody EvaluationScore evaluationScore)
    {
        return toAjax(evaluationScoreService.updateEvaluationScore(evaluationScore));
    }

    /**
     * 删除问卷得分
     */
    // @PreAuthorize("@ss.hasPermi('system:score:remove')")
    @Log(title = "问卷得分", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(evaluationScoreService.deleteEvaluationScoreByIds(ids));
    }

    /**
     * 检查用户是否已完成问卷评价
     */
    @GetMapping("/checkCompleted")
    public AjaxResult checkCompleted(String userName, Integer quesType)
    {
        EvaluationScore query = new EvaluationScore();
        query.setUserName(userName);
        query.setQuesType(quesType);
        
        List<EvaluationScore> list = evaluationScoreService.selectEvaluationScoreList(query);
        
        // 如果找到记录，说明已完成
        boolean completed = list != null && !list.isEmpty();
        
        return AjaxResult.success(completed);
    }
}
