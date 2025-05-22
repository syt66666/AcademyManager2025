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
import java.util.*;

public class DeepSeekClient {
    private static final Logger log = LoggerFactory.getLogger(DeepSeekClient.class);

    // JDK8兼容的敏感词集合初始化
    private static final Set<String> SENSITIVE_WORDS = new HashSet<String>() {{
        add("密码");
        add("处分");
        add("成绩修改");
    }};

    // JDK8兼容的部门映射初始化
    private static final Map<String, String> DEPARTMENT_MAPPING = new HashMap<String, String>() {{
        put("宿舍", "学生社区中心：0411-XXXXXXX");
        put("食堂", "后勤服务中心：0411-XXXXXXX");
        put("社团", "团委办公室：0411-XXXXXXX");
    }};

    private final WebClient webClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public DeepSeekClient(String apiKey, String endpoint) {
        this.webClient = WebClient.builder()
                .baseUrl(endpoint)
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .build();
    }

    public Mono<String> generateResponse(String prompt, String context) {
        // 服务边界检查
        String boundaryCheck = checkServiceBoundary(prompt);
        if (boundaryCheck != null) {
            return Mono.just(boundaryCheck);
        }

        return buildRequest(prompt, context)
                .flatMap(this::callApi)
                .map(this::processResponse)
                .map(this::validateResponse)
                .onErrorResume(e -> {
                    log.error("API调用异常: {}", e.getMessage());
                    return Mono.just("系统繁忙，请稍后再试");
                });
    }

    private Mono<Map<String, Object>> buildRequest(String prompt, String context) {
        return Mono.fromCallable(() -> {
            Map<String, Object> requestBody = new LinkedHashMap<>();
            requestBody.put("model", "deepseek-chat");

            List<Map<String, String>> messages = new ArrayList<>();
            messages.add(createSystemMessage(context));

            if (!isNewSession(prompt)) {
                messages.addAll(getHistoryMessages());
            }

            Map<String, String> userMsg = new HashMap<>();
            userMsg.put("role", "user");
            userMsg.put("content", prompt);
            messages.add(userMsg);

            requestBody.put("messages", messages);
            requestBody.put("temperature", 0.5);
            requestBody.put("max_tokens", 800);
            return requestBody;
        });
    }

    private Map<String, String> createSystemMessage(String context) {
        Map<String, String> systemMsg = new HashMap<>();
        systemMsg.put("role", "system");
        systemMsg.put("content", String.format(
                "【角色定义】\n" +
                        "大连理工大学官方智能助手，服务范围仅限于学业咨询（课程、学分、分流政策）\n\n" +
                        "【知识依据】\n" +
                        "<校园知识库>\n" +
                        "%s\n" +
                        "</校园知识库>\n\n" +
                        "【应答规则】\n" +
                        "1. 自然口语化回答，禁用\"首先、其次\"等结构化表述\n" +
                        "2. 政策条款必须标注版本（例：根据《2024分流办法》第X条）\n" +
                        "3. 遇到以下情况使用标准回复：\n" +
                        "   - 非学业问题 → \"建议联系：%%s\"\n" +
                        "   - 隐私数据 → \"请至教务处窗口办理\"\n" +
                        "   - 未知问题 → \"该问题需线下咨询教务处\"",
                sanitizeContext(context)));
        return systemMsg;
    }

    private String sanitizeContext(String context) {
        return context.replaceAll("\\b\\d{8}\\b", "***")  // 学号脱敏
                .replaceAll("\\b\\d{11}\\b", "***"); // 手机号脱敏
    }

    private Mono<String> callApi(Map<String, Object> requestBody) {
        log.debug("API请求体：{}", requestBody);
        return webClient.post()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .onStatus(HttpStatus::isError, response ->
                        response.bodyToMono(String.class)
                                .flatMap(error -> Mono.error(new RuntimeException("API异常: " + error)))
                )
                .bodyToMono(String.class);
    }

    private String processResponse(String jsonResponse) {
        try {
            JsonNode root = objectMapper.readTree(jsonResponse);
            if (!root.has("choices") || root.get("choices").size() == 0) {
                log.warn("无效响应结构：{}", jsonResponse);
                return "服务响应异常，请稍后重试";
            }

            JsonNode contentNode = root.path("choices").get(0)
                    .path("message")
                    .path("content");

            return cleanResponse(contentNode.asText());
        } catch (JsonProcessingException e) {
            log.error("JSON解析失败：{}", jsonResponse);
            return "响应解析异常，请稍后重试";
        }
    }

    private String cleanResponse(String content) {
        String cleaned = content.replaceAll("【.*?】", "")          // 移除方括号标签
                .replaceAll("\\d+[、.]\\s*", "")   // 移除数字编号
                .replaceAll("\\n{3,}", "\n\n");    // 合并空行

        if (!cleaned.startsWith("同学")) {
            cleaned = "同学你好！" + cleaned;
        }
        return cleaned.trim();
    }

    private String validateResponse(String content) {
        // 敏感词校验
        for (String word : SENSITIVE_WORDS) {
            if (content.contains(word)) {
                log.warn("触发敏感词拦截");
                return "请至教务处窗口办理";
            }
        }

        // 政策版本校验
        if (content.contains("《") && !content.contains("版")) {
            log.warn("政策版本缺失");
            return content.replaceAll("《(.*?)》", "《$1（2024版）》");
        }

        return content;
    }

    private String checkServiceBoundary(String prompt) {
        // 敏感问题拦截
        for (String word : SENSITIVE_WORDS) {
            if (prompt.contains(word)) {
                return "请至教务处窗口办理";
            }
        }

        // 部门分流
        for (Map.Entry<String, String> entry : DEPARTMENT_MAPPING.entrySet()) {
            if (prompt.contains(entry.getKey())) {
                return String.format("建议联系：%s", entry.getValue());
            }
        }
        return null;
    }

    private boolean isNewSession(String prompt) {
        return prompt.startsWith("[新会话]");
    }

    private List<Map<String, String>> getHistoryMessages() {
        // 实现时接入实际的历史记录存储
        return Collections.emptyList();
    }
}