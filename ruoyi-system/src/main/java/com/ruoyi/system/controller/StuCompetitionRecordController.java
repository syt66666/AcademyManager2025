package com.ruoyi.system.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.dto.StuCompetitionRecord;
import com.ruoyi.system.service.StuCompetitionRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/competition")
public class StuCompetitionRecordController extends BaseController {

    @Autowired
    private StuCompetitionRecordService service;

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
            service.insertStuCompetitionRecord(record);
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
     * 获取竞赛记录
     */
    @GetMapping("/records")
    public AjaxResult getCompetitionRecords(@RequestParam(value = "semester", required = false) String semester,
                                            StuCompetitionRecord record) {
        // 设置学期参数
        record.setSemester(semester);
        // 启动分页
        startPage();
        // 查询竞赛记录
        List<StuCompetitionRecord> records = service.getAllCompetitionRecords(record);
        // 返回分页数据
        return AjaxResult.success(getDataTable(records));
    }
}