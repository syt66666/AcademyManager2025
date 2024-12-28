package com.ruoyi.system.controller;

import com.ruoyi.system.domain.BaseResponse;
import com.ruoyi.system.domain.StudentInformation;
import com.ruoyi.system.service.StudentInformationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    /**
     * 批量导入学生数据
     * @param file
     * @return
     */
    @PostMapping("/excel/upload")
    public BaseResponse<StudentInformation> uploadExcel(MultipartFile file) {
        studentInformationService.multipleInsertStudent(file);
        return new BaseResponse<>(null, "导入数据成功");
    }

    /**
     * 插入一条学生数据
     * @param studentInformation
     * @return
     */
    @PostMapping("/insert")
    public BaseResponse<StudentInformation> insertStudentInformation(@RequestBody StudentInformation studentInformation) {
        if(studentInformation.getStudentId() == null || studentInformation.getStudentName() == null ||studentInformation.getStudentId().isEmpty() || studentInformation.getStudentName().isEmpty()){
            throw new IllegalArgumentException("学生ID或姓名不能为空");
        }
        studentInformationService.insertOneStudent(studentInformation);
        return new BaseResponse<>(null, "插入学生信息数据成功");
    }
}
