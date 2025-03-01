package com.ruoyi.system.controller;

import com.alibaba.fastjson2.JSON;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.dto.StuCompetitionRecord;
import com.ruoyi.system.service.StuCompetitionRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

@RestController
@RequestMapping("/competition")
public class StuCompetitionRecordController extends BaseController {

    @Autowired
    private StuCompetitionRecordService service;

    // 添加竞赛记录
    @PostMapping("/add")
    public AjaxResult addCompetitionRecord(@RequestParam("StuCompetitionRecord") String stucompetitionrecord,
                                           @RequestParam("proofMaterial") MultipartFile proofMaterial) {
        StuCompetitionRecord record = JSON.parseObject(stucompetitionrecord, StuCompetitionRecord.class);
        if (record.getCompetitionName() == null || record.getCompetitionName().isEmpty()) {
            return AjaxResult.error("竞赛名称不能为空");
        }
        if (record.getCompetitionLevel() == null || record.getCompetitionLevel().isEmpty()) {
            return AjaxResult.error("竞赛级别不能为空");
        }
        if (record.getAwardLevel() == null || record.getAwardLevel().isEmpty()) {
            return AjaxResult.error("几等奖不能为空");
        }

        // 图片大小不能超过10MB
        if (proofMaterial.getSize() > 10 * 1024 * 1024) {  // 10MB = 10 * 1024 * 1024 字节
            return AjaxResult.error("图片大小不能超过10MB");
        }
        record.setApplyTime(LocalDateTime.now());

        // 压缩图片并转换为字节数组
        try {
            BufferedImage originalImage = ImageIO.read(proofMaterial.getInputStream());
            int width = (int) (originalImage.getWidth() * 0.2);
            int height = (int) (originalImage.getHeight() * 0.2);

            Image scaledImage = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            BufferedImage compressedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = compressedImage.createGraphics();
            g2d.drawImage(scaledImage, 0, 0, null);
            g2d.dispose();

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(compressedImage, "jpg", byteArrayOutputStream);
            record.setProofMaterial(byteArrayOutputStream.toByteArray());
        } catch (Exception e) {
            return AjaxResult.error("图片处理失败: " + e.getMessage());
        }

        // 保存记录
        service.insertStuCompetitionRecord(record);
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