package com.ruoyi.system.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DeepSeekClient {
    private static final Logger log = LoggerFactory.getLogger(DeepSeekClient.class);

    private final WebClient webClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public DeepSeekClient(String apiKey, String endpoint) {
        this.webClient = WebClient.builder()
                .baseUrl(endpoint)
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .build();
    }

    public Mono<String> generateResponse(String prompt) {
        return buildRequest(prompt)
                .flatMap(this::callApi)
                .map(this::processResponse)
                .onErrorResume(e -> Mono.just("系统繁忙，请稍后再试"));
    }

    private Mono<Map<String, Object>> buildRequest(String prompt) {
        return Mono.fromCallable(() -> {
            Map<String, Object> requestBody = new LinkedHashMap<>();
            requestBody.put("model", "deepseek-chat");

            List<Map<String, String>> messages = new ArrayList<>();
            messages.add(createSystemMessage());

            Map<String, String> userMsg = new HashMap<>();
            userMsg.put("role", "user");
            userMsg.put("content", prompt);
            messages.add(userMsg);

            requestBody.put("messages", messages);
            requestBody.put("temperature", 0.7);
            return requestBody;
        });
    }

    private Map<String, String> createSystemMessage() {
        Map<String, String> systemMsg = new HashMap<>();
        systemMsg.put("role", "system");
        systemMsg.put("content", "作为大连理工大学官方助手，用自然口语化中文回答，避免使用任何章节标题。"
                + "回答结构：1.自然问候 2.分点解答 3.相关服务建议");
        return systemMsg;
    }

    private Mono<String> callApi(Map<String, Object> requestBody) {
        return webClient.post()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .onStatus(HttpStatus::isError, response ->
                        response.bodyToMono(String.class)
                                .flatMap(error -> Mono.error(new RuntimeException("API Error: " + error)))
                )
                .bodyToMono(String.class);
    }

    private String processResponse(String jsonResponse) {
        try {
            JsonNode root = objectMapper.readTree(jsonResponse);
            JsonNode contentNode = root.path("choices").get(0)
                    .path("message").path("content");
            return cleanResponse(contentNode.asText());
        } catch (JsonProcessingException e) {
            log.error("JSON解析失败: {}", jsonResponse);
            return "响应解析异常，请稍后重试";
        }
    }

    private String cleanResponse(String content) {
        // 移除结构化标签
        String cleaned = content.replaceAll("(?i)(亲切问候|主题内容|相关建议)[：:]?", "")
                .replaceAll("\\d+\\.\\s*", "");

        // 合并多余空行
        cleaned = cleaned.replaceAll("(\\n\\s*)+", "\n\n");

        // 确保自然开头
        if (!cleaned.startsWith("同学") && !cleaned.startsWith("老师")) {
            cleaned = "同学你好！" + cleaned;
        }
        return cleaned.trim();
    }
}