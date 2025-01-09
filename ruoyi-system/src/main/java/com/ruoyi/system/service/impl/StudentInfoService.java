package com.ruoyi.system.service.impl;

import com.ruoyi.system.domain.StudentInfo;
import com.ruoyi.system.mapper.StudentInfoMapper;
import com.ruoyi.system.service.IStudentInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StudentInfoService implements IStudentInfoService {

    @Autowired
    private StudentInfoMapper studentInfoMapper;

    public StudentInfo getStudentInfoById(String studentId) {
        return studentInfoMapper.selectByStudentId(studentId);
    }
}
