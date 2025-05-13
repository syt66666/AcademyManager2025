package com.ruoyi.system.service.impl;


import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.UserQuestionnaireAnswer;
import com.ruoyi.system.domain.dto.MajorStatistic;
import com.ruoyi.system.domain.dto.StuInfoDTO;
import com.ruoyi.system.mapper.StuInfoMapper;
import com.ruoyi.system.service.websocket.WebSocketUsers;
import com.ruoyi.system.domain.StuMajor;
import com.ruoyi.system.domain.dto.MajorStatisticDTO;
import com.ruoyi.system.mapper.StuMajorMapper;

import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

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

    @Autowired
    private StuInfoMapper stuInfoMapper;

    public Map<String, ?> echarts2() {
        Map<String, Object> result = new HashMap<>();

        // 1. 获取原始数据
        List<StuInfoDTO> stuInfoDTOS = stuInfoMapper.getAcademyNum();
        Map<String, Integer> beforeCnt = new HashMap<>();
        for (StuInfoDTO info : stuInfoDTOS) {
            String academy=info.getAcademy();
            Integer studentNum= info.getStudentNum();
            beforeCnt.put(academy, studentNum);
        }

        // 2. 处理分流后数据
        Map<String, Map<String, Integer>> afterCnt1 = new HashMap<>();
        Map<String, Map<String, Map<String, Integer>>> changeMajorType = new HashMap<>();

        List<MajorStatistic> answers = majorMapper.getMajorNum();
        for (MajorStatistic answer : answers) {
            String academy = answer.getAcademy();
            String type = String.valueOf(answer.getType());
            Integer studentNum = answer.getStudentNum();
            String majorName = answer.getMajorName();

            // 分流后统计：按学院-专业累加
            if (StringUtils.isNotEmpty(academy) && StringUtils.isNotEmpty(majorName)) {
                // 更新学院-专业人数
                Map<String, Integer> afterMap1 = afterCnt1.computeIfAbsent(academy, k -> new HashMap<>());
                afterMap1.put(majorName, afterMap1.getOrDefault(majorName, 0) + studentNum);

                // 更新分流类型分布
                Map<String, Map<String, Integer>> academyChangeType = changeMajorType.computeIfAbsent(academy, k -> new HashMap<>());
                Map<String, Integer> majorChangeType1 = academyChangeType.computeIfAbsent(majorName, k -> new HashMap<>());
                majorChangeType1.put(type, majorChangeType1.getOrDefault(type, 0) + studentNum);
            }
        }


        //map<String, map<String, Integer>> map
        result.put("beforeCnt", beforeCnt);
        result.put("afterCnt1", afterCnt1);
        result.put("changeMajorType", changeMajorType);
        return result;
    }

    public List<StuMajor> getAvailableMajors(@RequestParam("major") String major,
                                             @RequestParam("academy") String academy,
                                             @RequestParam("divertForm") String divertForm,
                                             @RequestParam("innovationStatus") Integer innovationStatus,
                                             @RequestParam("policyStatus") Integer policyStatus) {
        System.out.println(111 + "major: " + major + " academy: " + academy + " innovationStatus: " + innovationStatus + " policyStatus: " + policyStatus);
        return strategyFactory.getStrategy(innovationStatus, policyStatus)
                .getAvailableMajors(major, academy,divertForm);
    }

    @Transactional(rollbackFor = Exception.class)
    public List<JSONObject> getEveryMajorCount(Integer parentId, boolean isTell, String divertFrom) {
        List<JSONObject> result = new ArrayList<>();
        System.out.println("66666666666666666666666666666666666666666666");
        //得到父专业下面的所有子专业的数据

        //更新专业人数数据
        List<MajorStatisticDTO> majorStatistics = majorMapper.selectMajorStatisticGradesNum(parentId, divertFrom);

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
                })
                .collect(Collectors.toList());
        // 批量更新子专业
        majorMapper.batchUpdateMajors(updateList);
        // 更新父级数据
        majorMapper.updateStuMajor(
                parentId,
                fatherACount.get(),
                fatherBCount.get(),
                fatherCCount.get(),
                fatherTotal.get()
        );

        //获取所有类型的专业人数
        List<MajorStatisticDTO> majorStatistics2 = majorMapper.getMajorStatisticGradesNum(parentId);
        majorStatistics2.stream()
                .peek(dto -> {
                    // 构建响应数据
                    JSONObject json = new JSONObject();
                    json.put("majorName", dto.getMajorName());
                    json.put("countA", dto.getGradeA());
                    json.put("countB", dto.getGradeB());
                    json.put("countC", dto.getGradeC());
                    json.put("count", dto.getStudentNum());
                    result.add(json);
                })
                .collect(Collectors.toList());

        if (isTell) {
            List<MajorStatisticDTO> majorStatistics3 = majorMapper.getMajorStatisticGradesNum(parentId);
            // 创建包含详细数据的消息列表
            List<JSONObject> messages = majorStatistics3.stream()
                    .map(dto -> new JSONObject()
                            .fluentPut("type", "student_update")
                            .fluentPut("majorId", dto.getMajorId())
                            .fluentPut("majorName", dto.getMajorName())
                            .fluentPut("gradeA", dto.getGradeA())
                            .fluentPut("gradeB", dto.getGradeB())
                            .fluentPut("gradeC", dto.getGradeC())
                            .fluentPut("total", dto.getStudentNum()))
                    .collect(Collectors.toList());
            // 发送消息
            // 异步发送WebSocket消息
            CompletableFuture.runAsync(() -> {
                messages.forEach(message ->
                        // 发送给所有客户端（包括自己）
                        WebSocketUsers.sendMessageToAll(message.toJSONString())
                );
            });
        }
        return result;
    }
}