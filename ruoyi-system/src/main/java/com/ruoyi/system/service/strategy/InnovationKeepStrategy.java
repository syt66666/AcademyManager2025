package com.ruoyi.system.service.strategy;

import com.ruoyi.system.domain.StuMajor;
import com.ruoyi.system.mapper.StuMajorMapper;
import com.ruoyi.system.utils.MajorTreeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

// 创新班保持策略
@Service
public class InnovationKeepStrategy implements MajorStrategy {
    @Autowired
    private StuMajorMapper majorMapper;

    @Override
    @Cacheable(value = "majors", key = "#major + '_' + #academy + '_keep'")
    public List<StuMajor> getAvailableMajors(String major, String academy,String divertForm) {
        System.out.println("InnovationKeepStrategy.getAvailableMajors called");
        return MajorTreeUtils.buildTree(
                majorMapper.selectByAcademyAndType(major, academy, 3)
        );
    }
}
