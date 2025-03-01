package com.ruoyi.system.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.system.domain.StuCourse;
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
import com.ruoyi.system.domain.StuScore;
import com.ruoyi.system.service.IStuScoreService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;
import org.springframework.web.multipart.MultipartFile;

/**
 * 【请填写功能名称】Controller
 *
 * @author ruoyi
 * @date 2025-03-01
 */
@RestController
@RequestMapping("/system/score")
public class StuScoreController extends BaseController
{
    @Autowired
    private IStuScoreService stuScoreService;

    /**
     * 查询成绩列表
     */
    @PreAuthorize("@ss.hasPermi('system:score:list')")
    @GetMapping("/list")
    public TableDataInfo list(StuScore stuScore)
    {
        startPage();
        List<StuScore> list = stuScoreService.selectStuScoreList(stuScore);
        return getDataTable(list);
    }

    /**
     * 导出成绩列表
     */
    @PreAuthorize("@ss.hasPermi('system:score:export')")
    @Log(title = "导出成绩列表", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, StuScore stuScore)
    {
        List<StuScore> list = stuScoreService.selectStuScoreList(stuScore);
        ExcelUtil<StuScore> util = new ExcelUtil<StuScore>(StuScore.class);
        util.exportExcel(response, list, "【请填写功能名称】数据");
    }

    /**
     * 获取成绩详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:score:query')")
    @GetMapping(value = "/{scoreId}")
    public AjaxResult getInfo(@PathVariable("scoreId") Long scoreId)
    {
        return success(stuScoreService.selectStuScoreByScoreId(scoreId));
    }

    /**
     * 新增成绩信息
     */
    @PreAuthorize("@ss.hasPermi('system:score:add')")
    @Log(title = "新增成绩信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody StuScore stuScore)
    {
        return toAjax(stuScoreService.insertStuScore(stuScore));
    }

    /**
     * 修改成绩信息
     */
    @PreAuthorize("@ss.hasPermi('system:score:edit')")
    @Log(title = "修改成绩信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody StuScore stuScore)
    {
        return toAjax(stuScoreService.updateStuScore(stuScore));
    }

    /**
     * 删除成绩信息
     */
    @PreAuthorize("@ss.hasPermi('system:score:remove')")
    @Log(title = "删除成绩信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{scoreIds}")
    public AjaxResult remove(@PathVariable Long[] scoreIds)
    {
        return toAjax(stuScoreService.deleteStuScoreByScoreIds(scoreIds));
    }


    @Log(title = "成绩管理", businessType = BusinessType.IMPORT)
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception
    {
        ExcelUtil<StuScore> util = new ExcelUtil<StuScore>(StuScore.class);
        List<StuScore> userList = util.importExcel(file.getInputStream());
        LoginUser loginUser = getLoginUser();
        String operName = loginUser.getUsername();
        String message = stuScoreService.importScore(userList, updateSupport, operName);
        return AjaxResult.success(message);
    }

    @GetMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) {
        ExcelUtil<StuScore> util = new ExcelUtil<>(StuScore.class);
        util.importTemplateExcel(response, "课程数据");
    }
}
