package com.ruoyi.system.service.impl;


import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.system.domain.StuMajor;
import com.ruoyi.system.domain.dto.MajorStatistic;
import com.ruoyi.system.domain.dto.MajorStatisticDTO;
import com.ruoyi.system.mapper.StuMajorMapper;

import com.ruoyi.system.service.IDataChangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class MajorSelectionServiceImpl {

    @Autowired
    private StuMajorMapper majorMapper;

    @Autowired
    private StrategyFactory strategyFactory;

    @Resource
    public IDataChangeService dataChangeService;

    MajorSelectionServiceImpl(IDataChangeService dataChangeService) {
        this.dataChangeService = dataChangeService;
    }

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
//    public List<JSONObject> getEveryMajorCount(Integer majorId, String divertForm) {
//
//        //子专业
//        List<MajorStatistic> majorStatistics = majorMapper.selectMajorStatistics(majorId);
//        List<JSONObject> result = new ArrayList<>();
//        final String intraClassA = "可类内任选，并转专业";
//        final String intraClassB = "可类内任选，不能转专业";
//        final String intraDomain = "可域内任选，并转专业";
//        Integer fatherCount = 0;
//        for(MajorStatistic majorStatistic : majorStatistics) {
//            JSONObject majorStatisticJson = new JSONObject();
//            majorStatisticJson.put("majorName", majorStatistic.getSubMajorName());
//            if(divertForm.equals(intraClassA)||divertForm.equals(intraClassB)){
//                fatherCount += majorStatistic.getIntraClassCount();
//                majorMapper.updateStuMajor(majorStatistic.getSubMajorId(),majorStatistic.getIntraClassCount());
//                majorStatisticJson.put("count", majorStatistic.getIntraClassCount());
//                dataChangeService.notifyDataChange(majorStatistic.getSubMajorName(), majorStatistic.getIntraClassCount());
//            } else if(divertForm.equals(intraDomain)){
//                fatherCount += majorStatistic.getIntraDomainCount();
//                majorMapper.updateStuMajor(majorStatistic.getSubMajorId(),majorStatistic.getIntraDomainCount());
//                majorStatisticJson.put("count", majorStatistic.getIntraDomainCount());
//                dataChangeService.notifyDataChange(majorStatistic.getSubMajorName(), majorStatistic.getIntraDomainCount());
//            } else {
//                majorStatisticJson.put("count", 0);
//            }
//            majorMapper.updateStuMajor(majorId,fatherCount);
//            result.add(majorStatisticJson);
//        }
//        return result;
//    }
    @Transactional(rollbackFor = Exception.class)
    public List<JSONObject> getEveryMajorCount(Integer majorId) {
        List<MajorStatisticDTO> majorStatistics = majorMapper.selectMajorStatisticGradesNum(majorId);
        List<JSONObject> result = new ArrayList<>();

        // 初始化统计变量
        AtomicInteger fatherACount = new AtomicInteger(0);
        AtomicInteger fatherBCount = new AtomicInteger(0);
        AtomicInteger fatherCCount = new AtomicInteger(0);
        AtomicInteger fatherTotal = new AtomicInteger(0);

        // 批量处理子专业
        List<MajorStatisticDTO> updateList = majorStatistics.stream()
                .peek(dto -> {
                    // 累加父级数据
                    fatherACount.addAndGet(dto.getGradeA());
                    fatherBCount.addAndGet(dto.getGradeB());
                    fatherCCount.addAndGet(dto.getGradeC());
                    fatherTotal.addAndGet(dto.getStudentNum());

                    // 构建响应数据
                    JSONObject json = new JSONObject();
                    json.put("majorName", dto.getMajorName());
                    json.put("countA", dto.getGradeA());
                    json.put("countB", dto.getGradeB());
                    json.put("countC", dto.getGradeC());
                    json.put("count", dto.getStudentNum());
                    result.add(json);

                    // 异步通知
                    CompletableFuture.runAsync(() ->
                            dataChangeService.notifyDataChange(dto.getMajorName(), dto.getStudentNum())
                    );
                })
                .collect(Collectors.toList());

        // 批量更新子专业
        majorMapper.batchUpdateMajors(updateList);

        // 更新父级数据
        majorMapper.updateStuMajor(
                majorId,
                fatherACount.get(),
                fatherBCount.get(),
                fatherCCount.get(),
                fatherTotal.get()
        );

        return result;
    }
}