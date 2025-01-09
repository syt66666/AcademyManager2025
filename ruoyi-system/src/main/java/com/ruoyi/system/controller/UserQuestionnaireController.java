package com.ruoyi.system.controller;

import com.ruoyi.system.service.IUserQuestionnaireService;
import io.swagger.v3.oas.models.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserQuestionnaireController {

    @Autowired
    private IUserQuestionnaireService userQuestionnaireService;

    @GetMapping("/api/check-questionnaire-completed")
    public boolean checkQuestionnaireCompleted(@RequestParam String userName, @RequestParam int questionnaireId) {
        return userQuestionnaireService.checkQuestionnaireCompleted(userName, questionnaireId);
    }

    // 你可以添加其他API控制器方法来处理 `user_questionnaire_answers` 数据
}
