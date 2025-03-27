package com.ruoyi.system.service.impl;


import com.ruoyi.system.domain.StuMajor;
import com.ruoyi.system.mapper.StuMajorMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MajorSelectionServiceImpl {

    @Autowired
    private StuMajorMapper majorMapper;

    @Autowired
    private StrategyFactory strategyFactory;

    public List<StuMajor> getAvailableMajors(    @RequestParam("major") String major,
                                                  @RequestParam("academy") String academy,
                                                 @RequestParam("innovationStatus") Integer innovationStatus,
                                                 @RequestParam("policyStatus") Integer policyStatus) {
        System.out.println(111+"major: " + major + " academy: " + academy + " innovationStatus: " + innovationStatus + " policyStatus: " + policyStatus);
        return strategyFactory.getStrategy(innovationStatus, policyStatus)
                .getAvailableMajors(major, academy);
    }
}

