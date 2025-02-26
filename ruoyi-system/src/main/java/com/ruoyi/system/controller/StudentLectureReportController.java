package com.ruoyi.system.controller;


import com.alibaba.fastjson2.JSON;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.StudentLectureReport;
import com.ruoyi.system.service.StudentLectureReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;


/**
 * 学生讲座报告管理
 */
@RestController
@RequestMapping("/Lecture")
public class StudentLectureReportController {

    @Autowired
    private StudentLectureReportService studentLectureReportService;

    /**
     * 导入学生报告信息
     *
     * @param
     * @return
     */
    @PostMapping("/insert")
    public AjaxResult uploadExcel(@RequestParam("studentLectureReport") String studentLectureReport,
                                  @RequestParam("reportFeeling") MultipartFile reportFeeling, @RequestParam("picture") MultipartFile picture) {
        StudentLectureReport report = JSON.parseObject(studentLectureReport, StudentLectureReport.class);
        if (report.getTitle() == null || report.getTitle().isEmpty()) {
            return AjaxResult.error("题目不能为空");
        }
        if (report.getReporter() == null || report.getReporter().isEmpty()) {
            return AjaxResult.error("报告人不能为空");
        }
        if (report.getReportDate() == null) {
            return AjaxResult.error("报告时间不能为空");
        }
        if (report.getReportContent() == null || report.getReportContent().isEmpty()) {
            return AjaxResult.error("内容简介不能为空");
        }
        if (report.getLink() == null || report.getLink().isEmpty()) {
            return AjaxResult.error("链接不能为空");
        }
        // 判断图片大小是否超过10MB
        if (picture.getSize() > 10 * 1024 * 1024) {  // 10MB = 10 * 1024 * 1024 字节
            return AjaxResult.error("图片大小不能超过10MB");
        }
        // 判断图片大小是否超过500KB
        if (reportFeeling.getSize() > 500 * 1024) {
            return AjaxResult.error("报告文件大小不能超过500KB");
        }
        // 声明 g2d 和 byteArrayOutputStream，在 try 块外部，确保 finally 块能访问它们
        Graphics2D g2d = null;
        ByteArrayOutputStream byteArrayOutputStream = null;

        try {
            report.setReportFeeling(reportFeeling.getBytes());
            //下面为压缩图片
            // 将上传的图片转为 BufferedImage
            BufferedImage originalImage = ImageIO.read(picture.getInputStream());
            int width = (int) (originalImage.getWidth() * 0.2);
            int height = (int) (originalImage.getHeight() * 0.2);

            // 创建压缩后的图片
            Image scaledImage = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);

            // 将压缩后的图片转换为 BufferedImage
            BufferedImage compressedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            g2d = compressedImage.createGraphics(); // 在 try 块内初始化
            g2d.drawImage(scaledImage, 0, 0, null);

            // 将压缩后的图片转换为 byte[]
            byteArrayOutputStream = new ByteArrayOutputStream(); // 在 try 块内初始化
            ImageIO.write(compressedImage, "jpg", byteArrayOutputStream);

            // 保存压缩后的图片 byte[]
            report.setPicture(byteArrayOutputStream.toByteArray());
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        } finally {
            // 确保在 finally 块中关闭流
            if (g2d != null) {
                g2d.dispose();
            }
            if (byteArrayOutputStream != null) {
                try {
                    byteArrayOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace(); // 捕获异常并打印
                }
            }
        }
        studentLectureReportService.insertStudentLectureReport(report);
        System.out.println(13212312);
        return AjaxResult.success();
    }
}
