package com.ruoyi.system.controller;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.service.DeepSeekClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.Valid;

@RestController
@RequestMapping("/system/assistant")
public class AiAssistantController {

    @Autowired
    private DeepSeekClient deepSeekClient;

    @PostMapping("/chat")
    public AjaxResult chat(@Valid @RequestBody ChatRequest request) {
        try {
            String response = deepSeekClient.generateResponse(request.getMessage()).block();
            return AjaxResult.success(response);
        } catch (Exception e) {
            return AjaxResult.error("服务暂时不可用，请稍后重试").put("code", 500);
        }
    }

    public static class ChatRequest {
        private String message;
        private String lang = "zh";

        // Getters and Setters
        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getLang() {
            return lang;
        }

        public void setLang(String lang) {
            this.lang = lang;
        }
    }
}