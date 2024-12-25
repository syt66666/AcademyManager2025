package com.ruoyi.system.controller;

import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.domain.BaseResponse;
import com.ruoyi.system.domain.StudentInformation;
import com.ruoyi.system.service.StudentInformationService;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 学生信息管理操作
 */
@RestController
@RequestMapping("/student")
public class StudentController {

    @Autowired
    private StudentInformationService studentInformationService;

    @PostMapping("/excel/upload")
    public BaseResponse<StudentInformation> uploadExcel(MultipartFile file) {
        studentInformationService.multipleInsertStudent(file);
        return new BaseResponse<>(null, "导入数据成功");
    }
}
