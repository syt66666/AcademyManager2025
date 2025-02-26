package com.ruoyi.system.service.impl;

import com.ruoyi.system.domain.StuInfo;
import com.ruoyi.system.mapper.StuInfoMapper;
import com.ruoyi.system.service.IStuInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StuInfoService implements IStuInfoService {

    @Autowired
    private StuInfoMapper stuInfoMapper;

    public StuInfo getStudentInfoById(String studentId) {
        return stuInfoMapper.selectByStudentId(studentId);
    }
}
