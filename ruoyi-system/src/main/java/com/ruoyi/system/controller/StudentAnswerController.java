package com.ruoyi.system.controller;
import com.ruoyi.system.domain.StudentAnswerUpdateRequest;
import com.ruoyi.system.service.IStudentAnswerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class StudentAnswerController {

    @Autowired
    private IStudentAnswerService studentAnswerService;

    @PostMapping("/updateStudentAnswer")
    public Map<String, Object> updateStudentAnswer(@RequestBody StudentAnswerUpdateRequest request) {
        if (request.getStudentId() == null || request.getAfterMajor() == null ||
                request.getAfterAcademy() == null || request.getChangeMajorType() == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", 400);
            response.put("message", "缺少必要的参数");
            return response;
        }

        return (Map<String, Object>) studentAnswerService.updateStudentAnswer(request);
    }
}
