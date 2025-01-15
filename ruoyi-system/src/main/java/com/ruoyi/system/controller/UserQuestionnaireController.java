package com.ruoyi.system.controller;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.service.IUserQuestionnaireService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class UserQuestionnaireController {
    @Autowired
    private IUserQuestionnaireService userQuestionnaireService;

    @GetMapping("/api/check-questionnaire-completed")
    public boolean checkQuestionnaireCompleted(@RequestParam String userName, @RequestParam int questionnaireId) {
        return userQuestionnaireService.checkQuestionnaireCompleted(userName, questionnaireId);
    }
    /**
     * 查询不同学院转专业人数
     */
    @GetMapping("/system/questionnaire/echarts2")
    public AjaxResult echarts2(@RequestParam int questionnaireId)
    {
        try {
            Map<String, ?> result = userQuestionnaireService.echarts2(questionnaireId);
            return AjaxResult.success(result);
        } catch (Exception e) {
            return AjaxResult.error("获取数据失败: " + e.getMessage());
        }
    }
}
