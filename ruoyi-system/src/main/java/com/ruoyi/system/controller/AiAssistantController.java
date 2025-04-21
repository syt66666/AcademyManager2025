package com.ruoyi.system.controller;

import com.ruoyi.system.service.DeepSeekClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.core.domain.AjaxResult;

import java.util.Map;

@RestController
@RequestMapping("/system/assistant")
public class AiAssistantController {

    @Autowired
    private DeepSeekClient deepSeekClient;

    @PostMapping("/chat")
    public AjaxResult chat(@RequestBody Map<String, String> request) {
        String response = deepSeekClient.generateResponse(request.get("message")).block();
        return AjaxResult.success(response);
    }
}