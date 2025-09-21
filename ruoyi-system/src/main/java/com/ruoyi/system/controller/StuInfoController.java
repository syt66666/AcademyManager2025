package com.ruoyi.system.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.StuInfo;
import com.ruoyi.system.service.IStuInfoService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.system.domain.dto.StuInfoTemplateDTO;

/**
 * 学生信息Controller
 *
 * @author ruoyi
 * @date 2025-03-27
 */
@RestController
@RequestMapping("/system/student")
public class StuInfoController extends BaseController
{
    @Autowired
    private IStuInfoService stuInfoService;

    /**
     * 查询学生信息列表
     */
//    @PreAuthorize("@ss.hasPermi('system:info:list')")
    @GetMapping("/list")
    public TableDataInfo list(StuInfo stuInfo)
    {
        System.out.println("查询学生信息列表，参数: " + stuInfo);
        startPage();
        List<StuInfo> list = stuInfoService.selectStuInfoList(stuInfo);
        System.out.println("查询结果数量: " + (list != null ? list.size() : 0));
        return getDataTable(list);
    }

    /**
     * 导出学生信息列表
     */
    @Log(title = "学生信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, StuInfo stuInfo)
    {
        List<StuInfo> list = stuInfoService.selectStuInfoList(stuInfo);
        ExcelUtil<StuInfo> util = new ExcelUtil<StuInfo>(StuInfo.class);
        util.exportExcel(response, list, "学生信息数据");
    }

    /**
     * 新增学生信息
     */
    @Log(title = "学生信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody StuInfo stuInfo)
    {
        return toAjax(stuInfoService.insertStuInfo(stuInfo));
    }

    /**
     * 修改学生信息
     */
//    @PreAuthorize("@ss.hasPermi('system:info:edit')")
    @Log(title = "学生信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody StuInfo stuInfo)
    {
        return toAjax(stuInfoService.updateStuInfo(stuInfo));
    }

    /**
     * 删除学生信息
     */
    @Log(title = "学生信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(stuInfoService.deleteStuInfoByIds(ids));
    }
    /**
     * 获取指定学生信息
     */
    @GetMapping("/{studentId}")
    public Map<String, Object> getStudentInfoById(@PathVariable String studentId) {
        StuInfo stuInfo = stuInfoService.getStudentInfoById(studentId);
        Map<String, Object> response = new HashMap<>();
        if (stuInfo != null) {
            response.put("studentInfo", stuInfo);
        } else {
            response.put("message", "学生未找到");
        }
        return response;
    }
    /**
     * 获取用户昵称
     */
    @GetMapping("/nickName")
    public AjaxResult getNickName() {
        String userNick = getUserNick();
        System.out.println("获取用户昵称: " + userNick);
        return AjaxResult.success(userNick);
    }

    /**
     * 导入学生信息数据
     */
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception
    {
        ExcelUtil<StuInfo> util = new ExcelUtil<StuInfo>(StuInfo.class);
        List<StuInfo> stuInfoList = util.importExcel(file.getInputStream());
        LoginUser loginUser = getLoginUser();
        String operName = loginUser.getUsername();
        String message = stuInfoService.importStuInfo(stuInfoList, updateSupport, operName);
        return AjaxResult.success(message);
    }

    /**
     * 下载导入模板
     */
    @GetMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) {
        ExcelUtil<StuInfoTemplateDTO> util = new ExcelUtil<>(StuInfoTemplateDTO.class);
        util.importTemplateExcel(response, "学生信息数据");
    }
}

