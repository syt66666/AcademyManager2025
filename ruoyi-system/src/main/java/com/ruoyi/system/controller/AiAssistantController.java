package com.ruoyi.system.controller;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.domain.AcademicSummary;
import com.ruoyi.system.service.DeepSeekClient;
import com.ruoyi.system.service.StudentProfileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.Valid;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@RestController
@RequestMapping("/system/assistant")
public class AiAssistantController {
    private static final Logger log = LoggerFactory.getLogger(AiAssistantController.class);

    @Autowired
    private DeepSeekClient deepSeekClient;
    @Autowired
    private StudentProfileService profileService;

    @PostMapping("/chat")
    public AjaxResult chat(@Valid @RequestBody ChatRequest request,
                           @AuthenticationPrincipal User user) {
        try {
            String studentId = "20210000000";
            String context = buildDynamicContext(studentId, request.getMessage());

            String response = deepSeekClient.generateResponse(request.getMessage(), context)
                    .block();
            return AjaxResult.success(response);
        } catch (Exception e) {
            log.error("智能助手服务异常", e);
            return Objects.requireNonNull(AjaxResult.error("服务暂时不可用，请稍后重试")
                            .put("code", 500))
                    .put("sessionId", request.getSessionId());
        }
    }

    private String buildDynamicContext(String studentId, String question) throws ExecutionException, InterruptedException, TimeoutException {
        // 并行获取三类数据
        CompletableFuture<String> academicFuture = CompletableFuture.supplyAsync(() ->
                formatAcademic(profileService.getAcademicRecord(studentId))
        );

        CompletableFuture<String> competitionFuture = CompletableFuture.supplyAsync(() ->
                profileService.formatCompetitions(profileService.getCompetitions(studentId))
        );

        CompletableFuture<String> activityFuture = CompletableFuture.supplyAsync(() ->
                profileService.formatActivities(profileService.getArtSportsActivities(studentId))
        );

            return "【学生成长档案】\n" +
                    "学业成绩：\n" + academicFuture.get(2, TimeUnit.SECONDS) + "\n\n" +
                    "科创竞赛：\n" + competitionFuture.get(2, TimeUnit.SECONDS) + "\n\n" +
                    "文体活动：\n" + activityFuture.get(2, TimeUnit.SECONDS) + "\n\n" +
                    "【政策依据】\n" + "专业分流可选专业为软件工程、网络工程、集成电路、电子科学与技术";
    }

    private String formatAcademic(AcademicSummary summary) {
        return "GPA：" + summary.getGpa().setScale(2, RoundingMode.HALF_UP) + "\n" +
                "累计学分：" + summary.getTotalCredits() + "\n" +
                "核心课程表现：" + summary.getCorePerformance();
    }

    private String getRelatedPolicies(String question) {
        // 实际应接入政策查询服务
        return "根据《2025版书院专业分流管理办法》相关规定";
    }

    private String buildFallbackContext(String studentId) {
        return "基础学业信息：\n" +
                "GPA：" + profileService.getAcademicRecord(studentId).getGpa().setScale(2, RoundingMode.HALF_UP);
    }

    public static class ChatRequest {
        private String message;
        private String sessionId;

        // Getters and Setters
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public String getSessionId() { return sessionId; }
        public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    }
}