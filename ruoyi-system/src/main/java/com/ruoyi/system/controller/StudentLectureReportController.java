package com.ruoyi.system.controller;


import com.alibaba.fastjson2.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.system.domain.StudentLectureReport;
import com.ruoyi.system.domain.vo.StuLectureReportVo;
import com.ruoyi.system.service.StudentLectureReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.ruoyi.common.core.domain.AjaxResult.DATA_TAG;
import static com.ruoyi.common.core.domain.AjaxResult.MSG_TAG;


/**
 * 学生讲座报告管理
 */
@RestController
@RequestMapping("/Lecture")
public class StudentLectureReportController extends BaseController{

    @Value("${file.upload-dir}") // 注入配置路径
    private String uploadDir;
    @Autowired
    private StudentLectureReportService studentLectureReportService;

    /**
     * 删除学生讲座报告记录
     */
    @DeleteMapping("/delete/{reportIds}")
    public AjaxResult remove(@PathVariable Integer[] reportIds)
    {
        return toAjax(studentLectureReportService.deleteStuLectureReportByReportIds(reportIds));
    }

    /**
     * 获取学生报告信息
     * @return
     */
    @GetMapping("/records")
    public AjaxResult getCompetitionRecords(@RequestParam(value = "semester", required = false) String semester) {
        startPage();  // 启动分页
        List<StuLectureReportVo> records = studentLectureReportService.getAllCompetitionRecords(); // 查询记录
        return AjaxResult.success(getDataTable(records));  // 返回分页数据
    }

    /**
     * 修改学生报告信息
     *
     * @param
     * @return
     */
    @PutMapping("/update")
    public AjaxResult updateExcel(@RequestParam(value = "studentLectureReport", required = false) String studentLectureReport,
                                  @RequestParam(value = "reportFeeling", required = false) MultipartFile reportFeeling,
                                  @RequestParam(value = "reportPicture", required = false) MultipartFile[] reportPictures) {
        //拿到需要更新的报告信息并做校验
        AjaxResult ajaxResult = judgeAndSetReportInformation(studentLectureReport, reportFeeling, reportPictures);
        if(ajaxResult.isError()){
            return AjaxResult.error(ajaxResult.get(MSG_TAG).toString());
        }
        StudentLectureReport report = (StudentLectureReport)ajaxResult.get(DATA_TAG);
        //更新信息
        studentLectureReportService.updateStudentLectureReport(report);
        return AjaxResult.success();
    }

    /**
     * 导入学生报告信息
     *
     * @param
     * @return
     */
    @PostMapping("/insert")
    public AjaxResult uploadExcel(@RequestParam(value = "studentLectureReport", required = false) String studentLectureReport,
                                  @RequestParam(value = "reportFeeling", required = false) MultipartFile reportFeeling,
                                  @RequestParam(value = "reportPicture", required = false) MultipartFile[] reportPictures) {
        //拿到需要插入的报告信息并做校验
        AjaxResult ajaxResult = judgeAndSetReportInformation(studentLectureReport, reportFeeling, reportPictures);
        if(ajaxResult.isError()){
            return AjaxResult.error(ajaxResult.get(MSG_TAG).toString());
        }
        StudentLectureReport report = (StudentLectureReport)ajaxResult.get(DATA_TAG);
        //插入信息
        studentLectureReportService.insertStudentLectureReport(report);
        return AjaxResult.success();
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

    private AjaxResult judgeAndSetReportInformation(String studentLectureReport, MultipartFile reportFeeling, MultipartFile[] reportPictures){
        StudentLectureReport report = JSON.parseObject(studentLectureReport, StudentLectureReport.class);
        report.setReportAdmitTime(LocalDateTime.now());
        int auditStatus = report.getAuditStatus();
        if(auditStatus != 3){
            if (report.getReportTitle() == null || report.getReportTitle().isEmpty()) {
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
            if (report.getReportLink() == null || report.getReportLink().isEmpty()) {
                return AjaxResult.error("链接不能为空");
            }
            if (report.getLecturePoster() == null || report.getLecturePoster().isEmpty()) {
                return AjaxResult.error("报告海报不能为空");
            }
            if(reportFeeling == null && reportFeeling.isEmpty()){
                return AjaxResult.error("心得体会不能为空");
            }
            if (reportFeeling.getSize() > 500 * 1024) {
                return AjaxResult.error("心得体会文件大小不能超过500KB");
            }
            // 判断图片大小是否超过500KB
            if(reportPictures == null || reportPictures.length == 0) {
                return AjaxResult.error("现场图片不能为空");
            }
            if(reportPictures.length <= 3){
                return AjaxResult.error("现场图片不能少于3张");
            }
            if(reportPictures.length >= 5){
                return AjaxResult.error("现场图片不能多于5张");
            }
        }
        try {
            // 处理文件上传
            if (reportPictures != null && reportPictures.length > 0) {
                List<String> filePaths = new ArrayList<>();
                for (MultipartFile file : reportPictures) {
                    String filePath = saveFile(file);
                    filePaths.add(filePath);
                }
                report.setReportPicture(new ObjectMapper().writeValueAsString(filePaths));
            }

            //处理心得体会上传
            if(reportFeeling != null && !reportFeeling.isEmpty()){
                // 判断文件大小是否超过500KB
                if (reportFeeling.getSize() > 500 * 1024) {
                    return AjaxResult.error("报告文件大小不能超过500KB");
                }
                report.setReportFeeling(saveFile(reportFeeling));
            }
        } catch (Exception e) {
            logger.error("提交失败", e);
            return AjaxResult.error("提交失败：" + e.getMessage());
        }
        return AjaxResult.success(report);
    }
}
