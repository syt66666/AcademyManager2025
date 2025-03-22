package com.ruoyi.system.controller;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.system.domain.StuMentorshipRecord;
import com.ruoyi.system.domain.dto.MentorshipAuditDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.service.IStuMentorshipRecordService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/system/mentorship")
public class StuMentorshipRecordController extends BaseController {
    @Autowired
    private IStuMentorshipRecordService stuMentorshipRecordService;

    private static final Logger logger = LoggerFactory.getLogger(StuMentorshipRecordController.class);
    /**
     * 查询导师指导记录列表
     */
    //@PreAuthorize("@ss.hasPermi('system:mentorship:list')")
    @GetMapping("/list")
    public TableDataInfo list(StuMentorshipRecord stuMentorshipRecord) {
        logger.info("查询导师指导记录列表");
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
    public void export(HttpServletResponse response, StuMentorshipRecord stuMentorshipRecord) {
        List<StuMentorshipRecord> list = stuMentorshipRecordService.selectStuMentorshipRecordList(stuMentorshipRecord);
        ExcelUtil<StuMentorshipRecord> util = new ExcelUtil<StuMentorshipRecord>(StuMentorshipRecord.class);
        util.exportExcel(response, list, "导师指导记录数据");
    }

    /**
     * 获取导师指导记录详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:mentorship:query')")
    @GetMapping(value = "/{recordId}")
    public AjaxResult getInfo(@PathVariable("recordId") Integer recordId) {
        return success(stuMentorshipRecordService.selectStuMentorshipRecordByRecordId(recordId));
    }

    //TODO用的飞飞的配置路径
    @Value("${file.upload-dir}") // 注入配置路径
    private String uploadDir;

    /**
     * 新增导师指导记录
     */
    //@PreAuthorize("@ss.hasPermi('system:mentorship:add')")
    @Log(title = "导师指导记录", businessType = BusinessType.INSERT)
    @PostMapping

    public AjaxResult add(@RequestPart("record") StuMentorshipRecord stuMentorshipRecord,
                          @RequestPart(value = "summaryFile", required = false) MultipartFile summaryFile,
                          @RequestPart(value = "photoPaths", required = false) MultipartFile[] photoPaths,
                          @RequestHeader(value = "Authorization", required = false) String token) {

        logger.info("接收到的参数: " + stuMentorshipRecord);
        try {
            // 验证 Token
            if (token == null || !validateToken(token)) {
                return AjaxResult.error("认证失败");
            }
            stuMentorshipRecord.setSubmitTime(new Date()); // 确保新插入的数据有最新的时间戳
            System.out.println("接收到的文件参数: " + summaryFile);
            // 处理总结文档
            if (summaryFile != null && !summaryFile.isEmpty()) {
                String summaryPath = saveFile(summaryFile);
                stuMentorshipRecord.setSummaryFilePath(summaryPath);
                System.out.println("接收到的总结文档路径: " + summaryPath);
            }

            // 处理文件上传
            if (photoPaths != null && photoPaths.length > 0) {
                List<String> filePaths = new ArrayList<>();
                for (MultipartFile file : photoPaths) {
                    String filePath = saveFile(file);
                    filePaths.add(filePath);
                }
                stuMentorshipRecord.setPhotoPaths(new ObjectMapper().writeValueAsString(filePaths));
            }
            if (photoPaths != null) {
                logger.info("接收的图片文件个数: {}", photoPaths.length);
            } else {
                logger.warn("没有接收到图片++++++++++");
            }
            // 保存记录
            stuMentorshipRecord.setSubmitTime(new Date());
            stuMentorshipRecordService.insertStuMentorshipRecord(stuMentorshipRecord);
            return AjaxResult.success();
        } catch (Exception e) {
            logger.error("提交失败", e);
            return AjaxResult.error("提交失败：" + e.getMessage());
        }
        //return toAjax(stuMentorshipRecordService.insertStuMentorshipRecord(stuMentorshipRecord));
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

    /**
     * 修改导师指导记录
     */
    //@PreAuthorize("@ss.hasPermi('system:mentorship:edit')")
    @PutMapping
    public AjaxResult edit(@RequestPart("record") StuMentorshipRecord record,
                           @RequestPart(value = "photoPaths", required = false) MultipartFile[] photoPaths,
                           @RequestPart(value = "summaryFile", required = false) MultipartFile summaryFile) throws IOException {
        // 获取旧记录
        StuMentorshipRecord oldRecord = stuMentorshipRecordService.selectStuMentorshipRecordByRecordId(record.getRecordId());
        // ================= 处理总结文档 =================
        if (summaryFile != null && !summaryFile.isEmpty()) {
            // 情况1：上传了新文件
            String newPath = saveFile(summaryFile);
            record.setSummaryFilePath(newPath);
//            deleteFile(oldRecord.getSummaryFilePath()); // 删除旧文件
        } else if (record.getSummaryFilePath().isEmpty() && oldRecord.getSummaryFilePath() != null) {
            // 情况2：没有上传文件但清空了路径（表示删除）
//           deleteFile(oldRecord.getSummaryFilePath());
            record.setSummaryFilePath(null);
        } else {
            // 情况3：保持原文件不变
            record.setSummaryFilePath(oldRecord.getSummaryFilePath());
        }

        // ================= 处理图片文件 =================
        List<String> oldFiles = parseMaterialPaths(oldRecord.getPhotoPaths());

        // 处理文件合并逻辑
        List<String> finalFilePaths = new ArrayList<>();

        // 1. 添加保留的旧文件
        if (record.getExistingProofMaterial() != null) {
            finalFilePaths.addAll(record.getExistingProofMaterial());
        }

        // 2. 处理新上传文件
        if (photoPaths != null && photoPaths.length > 0) {
            for (MultipartFile file : photoPaths) {
                String filePath = saveFile(file);
                finalFilePaths.add(filePath);
            }
        }

        // 3. 更新文件路径到记录
        record.setPhotoPaths(new ObjectMapper().writeValueAsString(finalFilePaths));

        // 4. 清理被删除的旧文件
        List<String> filesToDelete = oldFiles.stream()
                .filter(oldFile -> !finalFilePaths.contains(oldFile))
                .collect(Collectors.toList());
        deleteOldFiles(filesToDelete);

        // 更新记录
        return toAjax(stuMentorshipRecordService.updateStuMentorshipRecord(record));

        }

    // 辅助方法：解析材料路径
    private List<String> parseMaterialPaths(String materialJson) throws IOException {
        if (materialJson == null || materialJson.isEmpty()) {
            return new ArrayList<>();
        }
        return new ObjectMapper().readValue(materialJson, new TypeReference<List<String>>() {
        });
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
     * 删除导师指导记录
     */
    //@PreAuthorize("@ss.hasPermi('system:mentorship:remove')")
    @Log(title = "导师指导记录", businessType = BusinessType.DELETE)
    @DeleteMapping("/{recordIds}")
    public AjaxResult remove(@PathVariable Integer[] recordIds) {
        return toAjax(stuMentorshipRecordService.deleteStuMentorshipRecordByRecordIds(recordIds));
    }

    @GetMapping("/auditList")
    public TableDataInfo auditList(StuMentorshipRecord stuMentorshipRecord)
    {
        startPage();
        List<StuMentorshipRecord> list = stuMentorshipRecordService.selectMentorshipRecordList(stuMentorshipRecord);
        return getDataTable(list);
    }

    @PostMapping("/checkUnique")
    public AjaxResult checkUnique(@RequestBody StuMentorshipRecord stuMentorshipRecord) {

        return stuMentorshipRecordService.checkUnique(stuMentorshipRecord);
    }

    /**
     * 更新审核信息
     */
    @PreAuthorize("@ss.hasPermi('system:mentorship:audit')")
    @Log(title = "导师指导审核", businessType = BusinessType.UPDATE)
    @PutMapping("/audit")
    public AjaxResult auditMentorship(@Validated @RequestBody MentorshipAuditDTO auditDTO) {
        // 构建更新参数
        StuMentorshipRecord mentorship = new StuMentorshipRecord();
        mentorship.setRecordId(auditDTO.getRecordId());
        mentorship.setAuditStatus(auditDTO.getAuditStatus());
        mentorship.setAuditRemark(auditDTO.getAuditRemark());
        mentorship.setAuditTime(new Date());
        // 执行更新操作
        return toAjax(stuMentorshipRecordService.updateMentorshipAuditInfo(mentorship));
    }

    @GetMapping("/auditCount")
    public AjaxResult getAuditStatusCount() {
        Map<String, Integer> countMap = stuMentorshipRecordService.countAuditStatus();
        return AjaxResult.success(countMap);
    }
}
