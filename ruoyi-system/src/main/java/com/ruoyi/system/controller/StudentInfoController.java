package com.ruoyi.system.controller;
import com.ruoyi.system.domain.StudentInfo;
import com.ruoyi.system.service.IStudentInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/student")
public class StudentInfoController {
    @Autowired
    private IStudentInfoService studentInfoService;
    @GetMapping("/{studentId}")
    public Map<String, Object> getStudentInfoById(@PathVariable String studentId) {
        StudentInfo studentInfo = studentInfoService.getStudentInfoById(studentId);
        Map<String, Object> response = new HashMap<>();
        if (studentInfo != null) {
            response.put("studentInfo", studentInfo);
        } else {
            response.put("message", "学生未找到");
        }

        return response;
    }
}
