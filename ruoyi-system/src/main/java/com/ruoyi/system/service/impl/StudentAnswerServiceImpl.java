package com.ruoyi.system.service.impl;

import com.ruoyi.system.domain.StudentAnswerUpdateRequest;
import com.ruoyi.system.mapper.StudentAnswerMapper;
import com.ruoyi.system.service.IStudentAnswerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class StudentAnswerServiceImpl implements IStudentAnswerService {

    @Autowired
    private StudentAnswerMapper studentAnswerMapper;

    @Override
    public Map<String, Object> updateStudentAnswer(StudentAnswerUpdateRequest request) {
        int rowsAffected = studentAnswerMapper.updateStudentAnswer(
                request.getAfterMajor(),
                request.getAfterAcademy(),
                request.getChangeMajorType(),
                request.getStudentId()
        );

        Map<String, Object> response = new HashMap<>();
        if (rowsAffected > 0) {
            response.put("code", 200);
            response.put("message", "更新成功");
        } else {
            response.put("code", 400);
            response.put("message", "未找到对应的学生ID");
        }

        return response;
    }
}
