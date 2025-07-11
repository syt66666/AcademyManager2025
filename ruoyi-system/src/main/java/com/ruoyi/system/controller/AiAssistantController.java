package com.ruoyi.system.controller;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.domain.AcademicSummary;
import com.ruoyi.system.domain.dto.QuestionClassifier;
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

        // 同步等待结果（JDK8没有orTimeout方法）
        String academic = academicFuture.get(2, TimeUnit.SECONDS);
        String competition = competitionFuture.get(2, TimeUnit.SECONDS);
        String activity = activityFuture.get(2, TimeUnit.SECONDS);
        // 构建中文提示词
        StringBuilder context = new StringBuilder()
                .append("## 角色设定\n")
                .append("你是大连理工大学官方智能助手，需严格遵循格式规范：\n\n")

                .append("## 学生档案\n")
                .append("### 学业表现\n").append(academic).append("\n")
                .append("### 竞赛经历\n").append(competition).append("\n")
                .append("### 文体活动\n").append(activity).append("\n\n");

        if (QuestionClassifier.isMajorRecommendationQuestion(question)) {
            context.append("## 应答规则[专业推荐模式]\n")
                    .append("1. 必须使用如下格式：\n")
                    .append("   ━━━━━━━━━━━━━━━━━━━━\n")
                    .append("   📚 专业匹配分析报告\n")
                    .append("   ━━━━━━━━━━━━━━━━━━━━\n")
                    .append("   🔍 匹配度评分：XX%（70-100区间）\n")
                    .append("   🏅 核心优势：\n")
                    .append("     ✔ 核心课程：[课程名]（成绩等级）\n")
                    .append("     ✔ 相关竞赛：[竞赛名称]（奖项级别）\n")
                    .append("   💡 分流建议：具体建议内容\n")
                    .append("   ━━━━━━━━━━━━━━━━━━━━\n\n")
                    .append("2. 按匹配度降序推荐1-3个专业\n")
                    .append("【政策依据】\n可选专业：软件工程、网络工程、集成电路设计与集成系统、电子科学与技术等\n\n");
        } else {
            context.append("## 应答规则[通用模式]\n")
                    .append("1. 校园生活问题：\n")
                    .append("   - 分步骤说明（使用❶❷❸符号）\n");
        }

        return context.toString();
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