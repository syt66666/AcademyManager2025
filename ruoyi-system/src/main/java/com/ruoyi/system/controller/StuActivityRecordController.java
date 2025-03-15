package com.ruoyi.system.controller;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson2.JSON;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.domain.StuCompetitionRecord;
import com.ruoyi.system.domain.StudentLectureReport;
import com.ruoyi.system.domain.dto.ActivityAuditDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.StuActivityRecord;
import com.ruoyi.system.service.IStuActivityRecordService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/system/activity")
public class StuActivityRecordController extends BaseController
{
    @Autowired
    private IStuActivityRecordService stuActivityRecordService;
    @Value("${file.upload-dir}") // 注入配置路径
    private String uploadDir;
    /**
     * 查询学生文体活动记录列表
     */
//    @PreAuthorize("@ss.hasPermi('system:activity:list')")
    @GetMapping("/list")
    public TableDataInfo list(StuActivityRecord stuActivityRecord)
    {
        startPage();
        List<StuActivityRecord> list = stuActivityRecordService.selectStuActivityRecordList(stuActivityRecord);
        return getDataTable(list);
    }

    /**
     * 导出学生文体活动记录列表
     */
    @PreAuthorize("@ss.hasPermi('system:activity:export')")
    @Log(title = "学生文体活动记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, StuActivityRecord stuActivityRecord)
    {
        List<StuActivityRecord> list = stuActivityRecordService.selectAuditActivityRecordList(stuActivityRecord);
        ExcelUtil<StuActivityRecord> util = new ExcelUtil<StuActivityRecord>(StuActivityRecord.class);
        util.exportExcel(response, list, "学生文体活动记录数据");
    }

    /**
     * 获取学生文体活动记录详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:activity:query')")
    @GetMapping(value = "/{activityId}")
    public AjaxResult getInfo(@PathVariable("activityId") Integer activityId)
    {
        return success(stuActivityRecordService.selectStuActivityRecordByActivityId(activityId));
    }

    /**
     * 新增学生文体活动记录
     */
//    @PreAuthorize("@ss.hasPermi('system:activity:add')")
    @Log(title = "学生文体活动记录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestPart("stuActivityRecord") String stuActivityRecordStr,
                          @RequestPart(value = "proofMaterial", required = false) MultipartFile[] proofMaterials,
                          @RequestHeader(value = "Authorization", required = false) String token)
    {
        try {
            // 验证 Token
            if (token == null || !validateToken(token)) {
                return AjaxResult.error("认证失败");
            }
            // 将 JSON 字符串转换为 StuActivityRecord 对象
            StuActivityRecord stuActivityRecord = new ObjectMapper().readValue(stuActivityRecordStr, StuActivityRecord.class);
            // 处理文件上传
            if (proofMaterials != null && proofMaterials.length > 0) {
                List<String> filePaths = new ArrayList<>();
                for (MultipartFile file : proofMaterials) {
                    String filePath = saveFile(file);
                    filePaths.add(filePath);
                }
                stuActivityRecord.setProofMaterial(new ObjectMapper().writeValueAsString(filePaths));
            }
            stuActivityRecord.setApplyTime(new Date());
            // 保存记录
            stuActivityRecordService.insertStuActivityRecord(stuActivityRecord);
            return AjaxResult.success();
        } catch (Exception e) {
            logger.error("提交失败", e);
            return AjaxResult.error("提交失败：" + e.getMessage());
        }

    }
    private String saveFile(MultipartFile file) throws IOException {
        // 创建标准化的上传路径
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();

        // 防御性创建目录
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
            System.out.println("创建上传目录：" + uploadPath);
        }

        // 生成安全文件名（UUID + 时间戳）
        String originalFileName = file.getOriginalFilename();
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));

        // 新文件名格式：UUID_时间戳.扩展名（示例：c9a3d7e0126345f8_1689234567890.jpg）
        String fileName = UUID.randomUUID().toString().replace("-", "")
                + "_"
                + System.currentTimeMillis()
                + extension;

        // 保存文件到指定位置
        Path targetPath = uploadPath.resolve(fileName);
        try (InputStream is = file.getInputStream()) {
            Files.copy(is, targetPath, StandardCopyOption.REPLACE_EXISTING);
        }

        return fileName; // 返回带时间戳的文件名
    }



    private boolean validateToken(String token) {
        // TODO: 实现 Token 验证逻辑
        return true; // 暂时返回 true
    }

    @GetMapping("/download")
    public void downloadFile(@RequestParam String filePath,
                             HttpServletResponse response) throws IOException {
        // 构建安全路径
        Path targetPath = Paths.get(uploadDir)
                .resolve(filePath)
                .normalize();

        // 路径合法性验证
        if (!targetPath.startsWith(Paths.get(uploadDir).normalize())) {
            response.sendError(403, "非法路径访问");
            return;
        }

        if (!Files.exists(targetPath)) {
            response.sendError(404, "文件不存在");
            return;
        }

        // 设置响应头（处理中文文件名）
        String encodedFileName = URLEncoder.encode(filePath, "UTF-8")
                .replace("+", "%20");
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition",
                "attachment; filename*=UTF-8''" + encodedFileName);

        // 高效传输文件
        Files.copy(targetPath, response.getOutputStream());
    }
    // 辅助方法：解析材料路径
    private List<String> parseMaterialPaths(String materialJson) throws IOException {
        if (materialJson == null || materialJson.isEmpty()) {
            return new ArrayList<>();
        }
        return new ObjectMapper().readValue(materialJson, new TypeReference<List<String>>() {});
    }
    // 辅助方法：删除旧文件
    private void deleteOldFiles(List<String> filePaths) {
        if (filePaths == null) return;

        for (String path : filePaths) {
            try {
                Path filePath = Paths.get(uploadDir).resolve(path).normalize();
                if (Files.exists(filePath)) {
                    Files.delete(filePath);
                }
            } catch (IOException e) {
                logger.warn("文件删除失败: {}", path, e);
            }
        }
    }
    /**
     * 修改学生文体活动记录
     */
//    @PreAuthorize("@ss.hasPermi('system:activity:edit')")
    @Log(title = "学生文体活动记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(
            @RequestPart("stuActivityRecord") StuActivityRecord record,
            @RequestPart(value = "proofMaterial", required = false) MultipartFile[] proofMaterials,
            @RequestHeader(value = "Authorization", required = false) String token) {
        try {
            // 验证 Token
            if (token == null || !validateToken(token)) {
                return AjaxResult.error("认证失败");
            }
            record.setApplyTime(new Date());
            // 获取旧记录用于清理文件
            StuActivityRecord oldRecord = stuActivityRecordService.selectStuActivityRecordByActivityId(record.getActivityId());
            List<String> oldFiles = parseMaterialPaths(oldRecord.getProofMaterial());

            // 处理文件合并逻辑
            List<String> finalFilePaths = new ArrayList<>();

            // 1. 添加保留的旧文件
            if (record.getExistingProofMaterial() != null) {
                finalFilePaths.addAll(record.getExistingProofMaterial());
            }

            // 2. 处理新上传文件
            if (proofMaterials != null && proofMaterials.length > 0) {
                for (MultipartFile file : proofMaterials) {
                    String filePath = saveFile(file);
                    finalFilePaths.add(filePath);
                }
            }
            // 3. 更新文件路径到记录
            record.setProofMaterial(new ObjectMapper().writeValueAsString(finalFilePaths));

            // 4. 清理被删除的旧文件
            List<String> filesToDelete = oldFiles.stream()
                    .filter(oldFile -> !finalFilePaths.contains(oldFile))
                    .collect(Collectors.toList());
            deleteOldFiles(filesToDelete);

            // 更新记录
            return toAjax(stuActivityRecordService.updateStuActivityRecord(record));
        } catch (Exception e) {
            logger.error("更新失败", e);
            return AjaxResult.error("更新失败：" + e.getMessage());
        }
    }

    /**
     * 删除学生文体活动记录
     */
//    @PreAuthorize("@ss.hasPermi('system:activity:remove')")
    @Log(title = "学生文体活动记录", businessType = BusinessType.DELETE)
    @DeleteMapping("/{activityIds}")
    public AjaxResult remove(@PathVariable Integer[] activityIds)
    {
        return toAjax(stuActivityRecordService.deleteStuActivityRecordByActivityIds(activityIds));
    }

    /**
     * 更新审核信息
     */
    @PreAuthorize("@ss.hasPermi('system:activity:audit')")
    @Log(title = "文体活动审核", businessType = BusinessType.UPDATE)
    @PutMapping("/audit")
    public AjaxResult auditActivity(@Validated @RequestBody ActivityAuditDTO auditDTO) {
        // 构建更新参数
        StuActivityRecord activity = new StuActivityRecord();
        activity.setActivityId(auditDTO.getActivityId());
        activity.setAuditStatus(auditDTO.getAuditStatus());
        activity.setAuditRemark(auditDTO.getAuditRemark());
        activity.setAuditTime(new Date());
        // 执行更新操作
        return toAjax(stuActivityRecordService.updateActivityAuditInfo(activity));
    }

    @GetMapping("/auditList")
    public TableDataInfo auditList(StuActivityRecord stuActivityRecord)
    {
        startPage();
        List<StuActivityRecord> list = stuActivityRecordService.selectAuditActivityRecordList(stuActivityRecord);
        return getDataTable(list);
    }

    @PostMapping("/checkUnique")
    public AjaxResult checkUnique(@RequestBody StuActivityRecord activity) {
        return stuActivityRecordService.checkUnique(activity);
    }

}
