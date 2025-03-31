package com.ruoyi.system.service.impl;


import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.system.domain.StuMajor;
import com.ruoyi.system.domain.dto.MajorStatistic;
import com.ruoyi.system.mapper.StuMajorMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
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

    /**
     *
     *
     * @param majorId
     * @param divertForm
     * @return
     */
    public List<JSONObject> getEveryMajorCount(Integer majorId, String divertForm) {

        //子专业
        List<MajorStatistic> majorStatistics = majorMapper.selectMajorStatistics(majorId);
        List<JSONObject> result = new ArrayList<>();
        final String intraClassA = "可类内任选，并转专业";
        final String intraClassB = "可类内任选，不能转专业";
        final String intraDomain = "可域内任选，并转专业";
        Integer fatherCount = 0;
        for(MajorStatistic majorStatistic : majorStatistics) {
            JSONObject majorStatisticJson = new JSONObject();
            majorStatisticJson.put("majorName", majorStatistic.getSubMajorName());
            if(divertForm.equals(intraClassA)||divertForm.equals(intraClassB)){
                fatherCount += majorStatistic.getIntraClassCount();
                majorMapper.updateStuMajor(majorStatistic.getSubMajorId(),majorStatistic.getIntraClassCount());
                majorStatisticJson.put("count", majorStatistic.getIntraClassCount());
            } else if(divertForm.equals(intraDomain)){
                fatherCount += majorStatistic.getIntraDomainCount();
                majorMapper.updateStuMajor(majorStatistic.getSubMajorId(),majorStatistic.getIntraDomainCount());
                majorStatisticJson.put("count", majorStatistic.getIntraDomainCount());
            } else {
                majorStatisticJson.put("count", 0);
            }
            majorMapper.updateStuMajor(majorId,fatherCount);
            result.add(majorStatisticJson);
        }
        return result;
    }

}

