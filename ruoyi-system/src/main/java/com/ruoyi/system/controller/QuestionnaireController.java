package com.ruoyi.system.controller;

import com.ruoyi.system.domain.QuestionnaireSubmitRequest;
import com.ruoyi.system.service.QuestionnaireService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/questionnaire")
public class QuestionnaireController {

    @Autowired
    private QuestionnaireService questionnaireService;

    @PostMapping("/submit")
    public ResponseEntity<?> submitQuestionnaire(@RequestBody QuestionnaireSubmitRequest request) {
        // 日志输出请求体数据
        System.out.println("收到的请求数据: " + request);

        // 检查请求体中的必要字段
        if (request.getUserName() == null ||
                request.getQuestionnaireId() == null ||
                request.getChangeAdress() == null ||
                request.getChangeMajor() == null ||
                request.getAfterChangeMajor() == null ||
                request.getAfterChangeAdress() == null ||
                request.getChangeMajorType() == null) {

            System.err.println("缺少必要的参数: " + request); // 记录缺少参数的信息
            HashMap<String,String> hashMap = new HashMap<>();
            hashMap.put("message", "缺少必要的参数");
            return ResponseEntity.badRequest().body(hashMap);
        }

        try {
            questionnaireService.submitQuestionnaire(request);
            HashMap<String,String> hashMap = new HashMap<>();
            hashMap.put("message", "提交成功");
            return ResponseEntity.ok(hashMap);
        } catch (Exception e) {
            e.printStackTrace();
            HashMap<String,String> hashMap = new HashMap<>();
            hashMap.put("message", "提交失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(hashMap);
        }
    }
}
