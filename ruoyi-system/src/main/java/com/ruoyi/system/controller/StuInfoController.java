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
import com.ruoyi.system.domain.dto.StuInfoExportDTO;
import com.ruoyi.system.service.IStuInfoService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.system.utils.StuInfoExcelTemplateUtil;
import com.ruoyi.system.utils.StuInfoExcelImportUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private static final Logger logger = LoggerFactory.getLogger(StuInfoController.class);
    
    @Autowired
    private IStuInfoService stuInfoService;
    
    @Autowired
    private StuInfoExcelTemplateUtil templateUtil;
    
    @Autowired
    private StuInfoExcelImportUtil excelImportUtil;

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
     * 导出学生信息列表（指定字段）
     */
    @Log(title = "学生信息", businessType = BusinessType.EXPORT)
    @PostMapping("/exportSelected")
    public void exportSelected(HttpServletResponse response, StuInfo stuInfo)
    {
        List<StuInfo> list = stuInfoService.selectStuInfoList(stuInfo);
        
        // 转换为导出DTO
        List<StuInfoExportDTO> exportList = list.stream().map(stu -> {
            StuInfoExportDTO dto = new StuInfoExportDTO();
            dto.setStudentId(stu.getStudentId());
            dto.setStudentName(stu.getStudentName());
            dto.setEnrollmentYear(stu.getEnrollmentYear());
            dto.setAcademy(stu.getAcademy());
            dto.setOriginalSystemMajor(stu.getOriginalSystemMajor());
            dto.setMajor(stu.getMajor());
            dto.setStudentClass(stu.getStudentClass());
            dto.setDivertForm(stu.getDivertForm());
            dto.setInnovationClass(stu.getInnovationClass());
            return dto;
        }).collect(java.util.stream.Collectors.toList());
        
        ExcelUtil<StuInfoExportDTO> util = new ExcelUtil<StuInfoExportDTO>(StuInfoExportDTO.class);
        util.exportExcel(response, exportList, "学生信息数据");
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
     * 重置学生密码
     */
    @Log(title = "学生信息", businessType = BusinessType.UPDATE)
    @PutMapping("/resetPassword/{id}")
    public AjaxResult resetPassword(@PathVariable Long id)
    {
        return toAjax(stuInfoService.resetStudentPassword(id));
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
        try {
            // 检查文件是否为空
            if (file == null || file.isEmpty()) {
                return AjaxResult.error("上传文件不能为空");
            }
            
            // 检查文件类型
            String fileName = file.getOriginalFilename();
            if (fileName == null || (!fileName.endsWith(".xlsx") && !fileName.endsWith(".xls"))) {
                return AjaxResult.error("只能上传Excel文件（.xlsx或.xls格式）");
            }
            
            // 检查文件大小（限制10MB）
            if (file.getSize() > 10 * 1024 * 1024) {
                return AjaxResult.error("文件大小不能超过10MB");
            }
            
            // 使用增强的Excel解析工具
            StuInfoExcelImportUtil.ExcelImportResult importResult = excelImportUtil.importExcel(file.getInputStream());
            
            logger.info("Excel解析结果 - 总行数: {}, 有效行数: {}, 空行数: {}", 
                importResult.getTotalRows(), importResult.getValidRows(), importResult.getNullRows());
            
            if (!importResult.isSuccess()) {
                logger.error("Excel解析失败: {}", importResult.getErrorMessage());
                return AjaxResult.error(importResult.getErrorMessage());
            }
            
            List<StuInfo> stuInfoList = importResult.getStuInfoList();
            
            // 调试：记录解析的数据详情
            if (stuInfoList != null && !stuInfoList.isEmpty()) {
                logger.info("解析到{}条学生数据", stuInfoList.size());
                for (int i = 0; i < Math.min(3, stuInfoList.size()); i++) {
                    StuInfo stuInfo = stuInfoList.get(i);
                    if (stuInfo != null) {
                        logger.info("第{}行数据 - 学号: {}, 姓名: {}, 书院: {}, 专业: {}, 行政班: {}, 分流形式: {}, 创新班: {} (类型: {})", 
                            i + 2, 
                            stuInfo.getStudentId() != null ? stuInfo.getStudentId() : "NULL",
                            stuInfo.getStudentName() != null ? stuInfo.getStudentName() : "NULL",
                            stuInfo.getAcademy() != null ? stuInfo.getAcademy() : "NULL",
                            stuInfo.getOriginalSystemMajor() != null ? stuInfo.getOriginalSystemMajor() : "NULL",
                            stuInfo.getStudentClass() != null ? stuInfo.getStudentClass() : "NULL",
                            stuInfo.getDivertForm() != null ? stuInfo.getDivertForm() : "NULL",
                            stuInfo.getInnovationClass() != null ? stuInfo.getInnovationClass() : "NULL",
                            stuInfo.getInnovationClass() != null ? stuInfo.getInnovationClass().getClass().getSimpleName() : "NULL");
                    } else {
                        logger.warn("第{}行数据为null", i + 2);
                    }
                }
            } else {
                logger.warn("解析结果为空列表");
            }
            
            // 如果有空行错误，给出警告但继续处理
            if (importResult.getNullRows() > 0) {
                logger.warn("Excel文件中发现{}行空数据", importResult.getNullRows());
            }
            
            LoginUser loginUser = getLoginUser();
            String operName = loginUser.getUsername();
            String message = stuInfoService.importStuInfo(stuInfoList, updateSupport, operName);
            return AjaxResult.success(message);
            
        } catch (Exception e) {
            // 记录详细错误信息
            logger.error("导入学生信息失败", e);
            return AjaxResult.error("导入失败：" + e.getMessage());
        }
    }

    /**
     * 下载导入模板
     */
    @GetMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) throws Exception {
        // 使用增强版模板生成工具，包含数据验证和填写说明
        templateUtil.generateTemplate(response);
    }
}

