package com.ruoyi.system.service.impl;
import com.ruoyi.system.service.IDataChangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author mabohv
 * @date 2025/3/30 20:14
 */

@Service
public class DataChangeServiceImpl implements IDataChangeService {

    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public DataChangeServiceImpl(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }


//    @Override
//    public void notifyDataChange(String entity, Integer entityId) {
//        Map<String, Object> payload = new HashMap<>();
//        payload.put("type", "DATA_CHANGE");
//        payload.put("timestamp", System.currentTimeMillis());
//        payload.put("entityType", entity);
//        payload.put("entityId", entityId);
//
//        // 推送到所有订阅的客户端
//        messagingTemplate.convertAndSend("/topic/data-updates", payload);
//    }
@Override
public void notifyDataChange(String majorName,
                             Integer gradeA,
                             Integer gradeB,
                             Integer gradeC,
                             Integer totalStudents) {
    Map<String, Object> payload = new HashMap<>();
    payload.put("type", "MAJOR_DATA_UPDATE");  // 更明确的类型标识
    payload.put("timestamp", System.currentTimeMillis());

    // 专业基本信息
    payload.put("majorName", majorName);  // 使用专业名作为标识

    // 统计信息
    Map<String, Object> stats = new HashMap<>();
    stats.put("gradeA", gradeA);
    stats.put("gradeB", gradeB);
    stats.put("gradeC", gradeC);
    stats.put("total", totalStudents);
    payload.put("statistics", stats);

    // 推送到所有订阅客户端
    messagingTemplate.convertAndSend("/topic/major-updates", payload);
}
}
