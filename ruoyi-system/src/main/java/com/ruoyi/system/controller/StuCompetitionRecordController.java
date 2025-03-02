package com.ruoyi.system.controller;

import com.alibaba.fastjson2.JSON;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.dto.StuCompetitionRecord;
import com.ruoyi.system.service.StuCompetitionRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

@RestController
@RequestMapping("/competition")
public class StuCompetitionRecordController extends BaseController {

    @Autowired
    private StuCompetitionRecordService service;

    @PostMapping("/add")
    public AjaxResult addCompetitionRecord(@RequestParam("StuCompetitionRecord") String stucompetitionrecord,
                                           @RequestParam("proofMaterial") MultipartFile proofMaterial) {
        // 解析 JSON 数据
        StuCompetitionRecord record = JSON.parseObject(stucompetitionrecord, StuCompetitionRecord.class);

        // 校验必填字段
        if (record.getCompetitionName() == null || record.getCompetitionName().isEmpty()) {
            return AjaxResult.error("竞赛名称不能为空");
        }
        if (record.getCompetitionLevel() == null || record.getCompetitionLevel().isEmpty()) {
            return AjaxResult.error("竞赛级别不能为空");
        }
        if (record.getAwardLevel() == null || record.getAwardLevel().isEmpty()) {
            return AjaxResult.error("几等奖不能为空");
        }

        // 校验图片大小
        if (proofMaterial.getSize() > 10 * 1024 * 1024) {  // 10MB = 10 * 1024 * 1024 字节
            return AjaxResult.error("图片大小不能超过10MB");
        }
        record.setApplyTime(LocalDateTime.now());

        // 处理图片
        try {
            // 读取原始图片
            BufferedImage originalImage = ImageIO.read(proofMaterial.getInputStream());
            if (originalImage == null) {
                return AjaxResult.error("图片格式不支持或文件已损坏");
            }

            // 计算缩放比例
            double scaleRatio = Math.min(
                    Math.min(2560.0 / originalImage.getWidth(), 1.0), // 限制最大宽度为 2560px
                    Math.min(1440.0 / originalImage.getHeight(), 1.0) // 限制最大高度为 1440px
            );

            int width = (int) (originalImage.getWidth() * scaleRatio);
            int height = (int) (originalImage.getHeight() * scaleRatio);

            // 缩放图片
            Image scaledImage = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            BufferedImage compressedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = compressedImage.createGraphics();
            g2d.drawImage(scaledImage, 0, 0, null);
            g2d.dispose();

            // 压缩图片并转换为字节数组
            try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
                ImageIO.write(compressedImage, "jpg", byteArrayOutputStream);
                record.setProofMaterial(byteArrayOutputStream.toByteArray()); // 设置图片数据
            }
        } catch (IOException e) {
            return AjaxResult.error("图片处理失败: " + e.getMessage());
        }

        // 插入记录到数据库
        try {
            service.insertStuCompetitionRecord(record);
        } catch (Exception e) {
            return AjaxResult.error("保存记录失败: " + e.getMessage());
        }

        return AjaxResult.success();
    }
    @GetMapping("/records")
    public AjaxResult getCompetitionRecords(StuCompetitionRecord record) {
        startPage();  // 启动分页
        List<StuCompetitionRecord> records = service.getAllCompetitionRecords(record);

        // 打印查询到的记录
        System.out.println("查询到的记录数量: " + records.size());
        for (StuCompetitionRecord r : records) {
            System.out.println("记录: " + r);
        }

        // 将二进制图片数据转换为 Base64
        for (StuCompetitionRecord r : records) {
            if (r.getProofMaterial() != null) {
                String base64Image = Base64.getEncoder().encodeToString(r.getProofMaterial());
                r.setProofMaterialBase64("data:image/jpeg;base64," + base64Image); // 设置 Base64 格式的图片
            }
        }

        // 返回分页数据
        return AjaxResult.success(getDataTable(records));
    }
}