package com.ruoyi.system.controller;

import com.ruoyi.system.domain.BaseResponse;
import com.ruoyi.system.domain.StudentInformation;
import com.ruoyi.system.service.StudentInformationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 学生信息管理操作
 */
@RestController
@RequestMapping("/student")
public class StudentInformationController {

    @Autowired
    private StudentInformationService studentInformationService;

    @PostMapping("/excel/upload")
    public BaseResponse<StudentInformation> uploadExcel(MultipartFile file) {
        studentInformationService.multipleInsertStudent(file);
        return new BaseResponse<>(null, "导入数据成功");
    }
}
