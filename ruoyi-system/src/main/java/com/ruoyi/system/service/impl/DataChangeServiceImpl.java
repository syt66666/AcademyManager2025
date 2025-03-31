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


    @Override
    public void notifyDataChange(String entity, Integer entityId) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("type", "DATA_CHANGE");
        payload.put("timestamp", System.currentTimeMillis());
        payload.put("entityType", entity);
        payload.put("entityId", entityId);

        // 推送到所有订阅的客户端
        messagingTemplate.convertAndSend("/topic/data-updates", payload);
    }
}
