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
        final String intraClass = "可类内任选，并转专业";
        final String intraDomain = "可域内任选，并转专业";
        for(MajorStatistic majorStatistic : majorStatistics) {
            JSONObject majorStatisticJson = new JSONObject();
            majorStatisticJson.put("majorName", majorStatistic.getSubMajorName());
            if(divertForm.equals(intraClass)){
                majorStatisticJson.put("count", majorStatistic.getIntraClassCount());
            } else if(divertForm.equals(intraDomain)){
                majorStatisticJson.put("count", majorStatistic.getIntraDomainCount());
            } else {
                majorStatisticJson.put("count", 0);
            }
            result.add(majorStatisticJson);
        }
        return result;
    }
}

