package com.ruoyi.system.controller;
import com.ruoyi.system.domain.StuInfo;
import com.ruoyi.system.service.IStuInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/system/student")
public class StuInfoController {
    @Autowired
    private IStuInfoService studentInfoService;
    @GetMapping("/{studentId}")
    public Map<String, Object> getStudentInfoById(@PathVariable String studentId) {
        StuInfo stuInfo = studentInfoService.getStudentInfoById(studentId);
        Map<String, Object> response = new HashMap<>();
        if (stuInfo != null) {
            response.put("studentInfo", stuInfo);
        } else {
            response.put("message", "学生未找到");
        }

        return response;
    }
}
