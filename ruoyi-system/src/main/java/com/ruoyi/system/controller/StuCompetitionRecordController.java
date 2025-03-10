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
import javax.servlet.http.HttpServletResponse;
import com.fasterxml.jackson.core.type.TypeReference;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.system.domain.dto.CompetitionAuditDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.StuCompetitionRecord;
import com.ruoyi.system.service.IStuCompetitionRecordService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/system/record")
public class StuCompetitionRecordController extends BaseController
{
    @Autowired
    private IStuCompetitionRecordService stuCompetitionRecordService;

    /**
     * 查询学生科创竞赛记录列表
     */
//    @PreAuthorize("@ss.hasPermi('system:record:list')")
    @GetMapping("/list")
    public TableDataInfo list(StuCompetitionRecord stuCompetitionRecord)
    {
        startPage();
        List<StuCompetitionRecord> list = stuCompetitionRecordService.selectStuCompetitionRecordList(stuCompetitionRecord);
        return getDataTable(list);
    }

    /**
     * 导出学生科创竞赛记录列表
     */
//    @PreAuthorize("@ss.hasPermi('system:record:export')")
    @Log(title = "学生科创竞赛记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, StuCompetitionRecord stuCompetitionRecord)
    {
        List<StuCompetitionRecord> list = stuCompetitionRecordService.selectStuCompetitionRecordList(stuCompetitionRecord);
        ExcelUtil<StuCompetitionRecord> util = new ExcelUtil<StuCompetitionRecord>(StuCompetitionRecord.class);
        util.exportExcel(response, list, "学生科创竞赛记录数据");
    }

    /**
     * 获取学生科创竞赛记录详细信息
     */
//    @PreAuthorize("@ss.hasPermi('system:record:query')")
    @GetMapping(value = "/{competitionId}")
    public AjaxResult getInfo(@PathVariable("competitionId") Integer competitionId)
    {
        return success(stuCompetitionRecordService.selectStuCompetitionRecordByCompetitionId(competitionId));
    }

    /**
     * 修改学生科创竞赛记录（支持文件上传）
     */
    @Log(title = "学生科创竞赛记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(
            @RequestPart("record") StuCompetitionRecord record,
            @RequestPart(value = "proofMaterial", required = false) MultipartFile[] proofMaterials,
            @RequestHeader(value = "Authorization", required = false) String token) {
        try {
            // 验证 Token
            if (token == null || !validateToken(token)) {
                return AjaxResult.error("认证失败");
            }

            // 获取旧记录用于清理文件
            StuCompetitionRecord oldRecord = stuCompetitionRecordService.selectStuCompetitionRecordByCompetitionId(record.getCompetitionId());
            List<String> oldFiles = parseMaterialPaths(oldRecord.getProofMaterial());

            // 处理新文件上传
            if (proofMaterials != null && proofMaterials.length > 0) {
                List<String> newFilePaths = new ArrayList<>();
                for (MultipartFile file : proofMaterials) {
                    String filePath = saveFile(file);
                    newFilePaths.add(filePath);
                }
                record.setProofMaterial(new ObjectMapper().writeValueAsString(newFilePaths));

                // 清理旧文件
                deleteOldFiles(oldFiles);
            } else {
                // 保留原有文件路径
                record.setProofMaterial(oldRecord.getProofMaterial());
            }

            // 更新记录
            return toAjax(stuCompetitionRecordService.updateStuCompetitionRecord(record));
        } catch (Exception e) {
            logger.error("更新失败", e);
            return AjaxResult.error("更新失败：" + e.getMessage());
        }
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
     * 删除学生科创竞赛记录
     */
//    @PreAuthorize("@ss.hasPermi('system:record:remove')")
    @Log(title = "学生科创竞赛记录", businessType = BusinessType.DELETE)
    @DeleteMapping("/{competitionIds}")
    public AjaxResult remove(@PathVariable Integer[] competitionIds)
    {
        return toAjax(stuCompetitionRecordService.deleteStuCompetitionRecordByCompetitionIds(competitionIds));
    }



    @Value("${file.upload-dir}") // 注入配置路径
    private String uploadDir;

    /**
     * 新增竞赛记录
     */
    @PostMapping("/add")
    public AjaxResult add(
            @RequestPart("record") StuCompetitionRecord record, // 直接绑定对象
            @RequestPart(value = "proofMaterial", required = false) MultipartFile[] proofMaterials,
            @RequestHeader(value = "Authorization", required = false) String token) {
        try {
            // 验证 Token
            if (token == null || !validateToken(token)) {
                return AjaxResult.error("认证失败");
            }
            record.setApplyTime(new Date()); // 确保新插入的数据有最新的时间戳
            // 处理文件上传
            if (proofMaterials != null && proofMaterials.length > 0) {
                List<String> filePaths = new ArrayList<>();
                for (MultipartFile file : proofMaterials) {
                    String filePath = saveFile(file);
                    filePaths.add(filePath);
                }
                record.setProofMaterial(new ObjectMapper().writeValueAsString(filePaths));
            }
            // 保存记录
            stuCompetitionRecordService.insertStuCompetitionRecord(record);
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

    /**
     * 更新审核信息
     */
    @Log(title = "科创竞赛审核", businessType = BusinessType.UPDATE)
    @PutMapping("/audit")
    public AjaxResult auditCompetition(@Validated @RequestBody CompetitionAuditDTO auditDTO) {
        // 构建更新参数
        StuCompetitionRecord competition = new StuCompetitionRecord();
        competition.setCompetitionId(auditDTO.getCompetitionId());
        competition.setAuditStatus(auditDTO.getAuditStatus());
        competition.setAuditRemark(auditDTO.getAuditRemark());
        competition.setAuditTime(new Date());
        // 执行更新操作
        return toAjax(stuCompetitionRecordService.updateCompetitionAuditInfo(competition));
    }

    @GetMapping("/auditList")
    public TableDataInfo auditList(StuCompetitionRecord stuCompetitionRecord)
    {
        startPage();
        List<StuCompetitionRecord> list = stuCompetitionRecordService.selectCompetitionRecordList(stuCompetitionRecord);
        return getDataTable(list);
    }
}
