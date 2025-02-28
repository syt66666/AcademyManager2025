package com.ruoyi.system.controller;

import com.alibaba.fastjson2.JSON;
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
import java.io.IOException;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/competition")
public class StuCompetitionRecordController {
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
        if (record.getAwardLevel() == null|| record.getAwardLevel().isEmpty()) {
            return AjaxResult.error("几等奖不能为空");
        }


        // 图片大小不能超过10MB
        if (proofMaterial.getSize() > 10 * 1024 * 1024) {  // 10MB = 10 * 1024 * 1024 字节
            return AjaxResult.error("图片大小不能超过10MB");
        }
        record.setApplyTime(LocalDateTime.now());
        // Graphics2D 用于绘制和压缩图像，ByteArrayOutputStream 用于存储压缩后的图片数据
        Graphics2D g2d = null;
        ByteArrayOutputStream byteArrayOutputStream = null;
        try {
            // 设置证明材料的字节内容
            record.setProofMaterial(proofMaterial.getBytes());

            // 读取原始图片
            BufferedImage originalImage = ImageIO.read(proofMaterial.getInputStream());

            // 设置压缩后的图片宽高（原图的 20%）
            int width = (int) (originalImage.getWidth() * 0.2);
            int height = (int) (originalImage.getHeight() * 0.2);

            // 缩放图片
            Image scaledImage = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);

            // 创建压缩后的 BufferedImage
            BufferedImage compressedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            g2d = compressedImage.createGraphics();

            // 绘制压缩后的图像
            g2d.drawImage(scaledImage, 0, 0, null);

            // 将压缩后的图片写入 ByteArrayOutputStream
            byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(compressedImage, "jpg", byteArrayOutputStream);

            // 设置压缩后的图片字节内容到 StuCompetitionRecord 对象
            record.setProofMaterial(byteArrayOutputStream.toByteArray());

        } catch (Exception e) {
            // 发生异常时，返回错误信息
            return AjaxResult.error(e.getMessage());
        } finally {
            // 释放资源
            if (g2d != null) {
                g2d.dispose();
            }
            if (byteArrayOutputStream != null) {
                try {
                    byteArrayOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace(); // 捕获并打印异常
                }
            }
        }

        // 将竞争记录保存到数据库或执行其他操作
        service.insertStuCompetitionRecord(record);
        return AjaxResult.success();
    }

    /*// 删除竞赛记录
    @DeleteMapping("/delete/{competitionId}")
    public String deleteCompetitionRecord(@PathVariable("competitionId") Integer competitionId) {
        int result = service.deleteStuCompetitionRecord(competitionId);
        return result > 0 ? "删除成功" : "删除失败";
    }

    // 更新竞赛记录
    @PutMapping("/update")
    public String updateCompetitionRecord(@RequestBody StuCompetitionRecord record) {
        int result = service.updateStuCompetitionRecord(record);
        return result > 0 ? "更新成功" : "更新失败";
    }

    // 获取单个竞赛记录
    @GetMapping("/get/{competitionId}")
    public StuCompetitionRecord getCompetitionRecord(@PathVariable("competitionId") Integer competitionId) {
        return service.getStuCompetitionRecordById(competitionId);
    }

    // 获取所有竞赛记录
    @GetMapping("/list")
    public List<StuCompetitionRecord> getAllCompetitionRecords() {
        return service.getAllStuCompetitionRecords();
    }*/
}
