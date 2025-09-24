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
import com.ruoyi.system.domain.CourseScore;
import com.ruoyi.system.service.ICourseScoreService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 课程成绩Controller
 * 
 * @author ruoyi
 * @date 2025-09-24
 */
@RestController
@RequestMapping("/system/score")
public class CourseScoreController extends BaseController
{
    @Autowired
    private ICourseScoreService courseScoreService;

    /**
     * 查询课程成绩列表
     */
    @PreAuthorize("@ss.hasPermi('system:score:list')")
    @GetMapping("/list")
    public TableDataInfo list(CourseScore courseScore)
    {
        startPage();
        List<CourseScore> list = courseScoreService.selectCourseScoreList(courseScore);
        return getDataTable(list);
    }

    /**
     * 导出课程成绩列表
     */
    @PreAuthorize("@ss.hasPermi('system:score:export')")
    @Log(title = "课程成绩", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, CourseScore courseScore)
    {
        List<CourseScore> list = courseScoreService.selectCourseScoreList(courseScore);
        ExcelUtil<CourseScore> util = new ExcelUtil<CourseScore>(CourseScore.class);
        util.exportExcel(response, list, "课程成绩数据");
    }

    /**
     * 获取课程成绩详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:score:query')")
    @GetMapping(value = "/{scoreId}")
    public AjaxResult getInfo(@PathVariable("scoreId") Long scoreId)
    {
        return success(courseScoreService.selectCourseScoreByScoreId(scoreId));
    }

    /**
     * 新增课程成绩
     */
    @PreAuthorize("@ss.hasPermi('system:score:add')")
    @Log(title = "课程成绩", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody CourseScore courseScore)
    {
        return toAjax(courseScoreService.insertCourseScore(courseScore));
    }

    /**
     * 修改课程成绩
     */
    @PreAuthorize("@ss.hasPermi('system:score:edit')")
    @Log(title = "课程成绩", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CourseScore courseScore)
    {
        return toAjax(courseScoreService.updateCourseScore(courseScore));
    }

    /**
     * 删除课程成绩
     */
    @PreAuthorize("@ss.hasPermi('system:score:remove')")
    @Log(title = "课程成绩", businessType = BusinessType.DELETE)
	@DeleteMapping("/{scoreIds}")
    public AjaxResult remove(@PathVariable Long[] scoreIds)
    {
        return toAjax(courseScoreService.deleteCourseScoreByScoreIds(scoreIds));
    }
}

