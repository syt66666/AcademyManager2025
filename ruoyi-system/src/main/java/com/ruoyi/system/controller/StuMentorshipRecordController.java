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
import com.ruoyi.system.domain.StuMentorshipRecord;
import com.ruoyi.system.service.IStuMentorshipRecordService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

@RestController
@RequestMapping("/system/mentorship")
public class StuMentorshipRecordController extends BaseController
{
    @Autowired
    private IStuMentorshipRecordService stuMentorshipRecordService;

    /**
     * 查询导师指导记录列表
     */
    //@PreAuthorize("@ss.hasPermi('system:mentorship:list')")
    @GetMapping("/list")
    public TableDataInfo list(StuMentorshipRecord stuMentorshipRecord)
    {
        startPage();
        List<StuMentorshipRecord> list = stuMentorshipRecordService.selectStuMentorshipRecordList(stuMentorshipRecord);
        return getDataTable(list);
    }

    /**
     * 导出导师指导记录列表
     */
    @PreAuthorize("@ss.hasPermi('system:mentorship:export')")
    @Log(title = "导师指导记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, StuMentorshipRecord stuMentorshipRecord)
    {
        List<StuMentorshipRecord> list = stuMentorshipRecordService.selectStuMentorshipRecordList(stuMentorshipRecord);
        ExcelUtil<StuMentorshipRecord> util = new ExcelUtil<StuMentorshipRecord>(StuMentorshipRecord.class);
        util.exportExcel(response, list, "导师指导记录数据");
    }

    /**
     * 获取导师指导记录详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:mentorship:query')")
    @GetMapping(value = "/{recordId}")
    public AjaxResult getInfo(@PathVariable("recordId") Integer recordId)
    {
        return success(stuMentorshipRecordService.selectStuMentorshipRecordByRecordId(recordId));
    }

    /**
     * 新增导师指导记录
     */
    //@PreAuthorize("@ss.hasPermi('system:mentorship:add')")
    @Log(title = "导师指导记录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody StuMentorshipRecord stuMentorshipRecord)
    {
        return toAjax(stuMentorshipRecordService.insertStuMentorshipRecord(stuMentorshipRecord));
    }

    /**
     * 修改导师指导记录
     */
    //@PreAuthorize("@ss.hasPermi('system:mentorship:edit')")
    @Log(title = "导师指导记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody StuMentorshipRecord stuMentorshipRecord)
    {
        return toAjax(stuMentorshipRecordService.updateStuMentorshipRecord(stuMentorshipRecord));
    }

    /**
     * 删除导师指导记录
     */
    //@PreAuthorize("@ss.hasPermi('system:mentorship:remove')")
    @Log(title = "导师指导记录", businessType = BusinessType.DELETE)
    @DeleteMapping("/{recordIds}")
    public AjaxResult remove(@PathVariable Integer[] recordIds)
    {
        return toAjax(stuMentorshipRecordService.deleteStuMentorshipRecordByRecordIds(recordIds));
    }
}
