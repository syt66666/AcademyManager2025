package com.ruoyi.system.controller;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.service.ISysQuestionnaireService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/system/questionnaire")
public class SysQuestionnaireController extends BaseController {

    @Autowired
    private ISysQuestionnaireService questionnaireService;

    @GetMapping("/times")
    public AjaxResult getQuestionnaireTimes(@RequestParam String questionnaireId) {
        Map<String, Object> times = questionnaireService.getQuestionnaireTimes(questionnaireId);
        if (times != null) {
            return AjaxResult.success(times);
        } else {
            return AjaxResult.error("问卷不存在");
        }
    }

}

