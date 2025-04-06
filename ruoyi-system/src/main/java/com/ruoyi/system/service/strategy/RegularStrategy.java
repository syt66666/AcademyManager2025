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
    public List<StuMajor> getAvailableMajors(String major,String academy,String divertForm) {
        List<StuMajor> majors = new ArrayList<>();
        System.out.println(111+"major: " + major + " academy: " + academy+ " divertForm: " + divertForm);
        if ("可类内任选，并转专业".equals(divertForm)
                || "可类内任选，不能转专业".equals(divertForm)){
            majors.addAll(majorMapper.selectByAcademyAndType(major, academy, 1)); // 类内
        }else if("可域内任选，并转专业".equals(divertForm)){
            majors.addAll(majorMapper.selectByAcademyAndType(major, academy, 2)); // 域内
        } else if ("仅可转专业".equals(divertForm)) {
            majors.addAll(majorMapper.selectByAcademyAndType(major, academy, 5)); // 仅可转专业
        } else if ("不可变更专业".equals(divertForm)) {
            majors.addAll(majorMapper.selectByAcademyAndType(major, academy, 6)); // 不可转专业
        }

        return MajorTreeUtils.buildTree(majors);
    }
}