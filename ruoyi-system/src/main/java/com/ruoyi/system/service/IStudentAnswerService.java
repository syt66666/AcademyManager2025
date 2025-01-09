package com.ruoyi.system.service;

import com.ruoyi.system.domain.StudentAnswerUpdateRequest;

import java.util.Map;

public interface IStudentAnswerService {
    Map<String, Object> updateStudentAnswer(StudentAnswerUpdateRequest request);
}
