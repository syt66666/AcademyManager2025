package com.ruoyi.system.service.strategy;


import com.ruoyi.system.domain.StuMajor;
import com.ruoyi.system.mapper.StuMajorMapper;
import com.ruoyi.system.utils.MajorTreeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

// 普通学生策略
@Service
public class RegularStrategy implements MajorStrategy {
    @Autowired
    private StuMajorMapper majorMapper;

    @Override
    public List<StuMajor> getAvailableMajors(String major,String academy) {
        List<StuMajor> majors = new ArrayList<>();
        System.out.println(111+"major: " + major + " academy: " + academy );
        majors.addAll(majorMapper.selectByAcademyAndType(major, academy, 1)); // 类内
        majors.addAll(majorMapper.selectByAcademyAndType(major, academy, 2)); // 域内
        return MajorTreeUtils.buildTree(majors);
    }
}