package com.ruoyi.system.controller;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.system.domain.QuestionnaireAnswer;
import com.ruoyi.system.domain.StuInfo;
import com.ruoyi.system.domain.UserQuestionnaireAnswer;
import com.ruoyi.system.service.IStuInfoService;
import com.ruoyi.system.service.IUserQuestionnaireService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class UserQuestionnaireController extends BaseController {
    @Autowired
    private IUserQuestionnaireService userQuestionnaireService;
    @Autowired
    private IStuInfoService stuInfoService;

    //检查学生是否已经填写问卷
    @GetMapping("/system/questionnaire/checkCompleted")
    public boolean checkQuestionnaireCompleted(@RequestParam String userName, @RequestParam int questionnaireId) {
        return userQuestionnaireService.checkQuestionnaireCompleted(userName, questionnaireId);
    }

    //查询不同学院转专业人数
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

    //查询学生提交的问卷答案
    @GetMapping("/system/questionnaire/answers")
    public TableDataInfo getStudentQuestionnaireAnswers(StuInfo stuInfo) {
        startPage();
        List<StuInfo> answers = stuInfoService.getStudentChangeMajorInfo(stuInfo);
        return getDataTable(answers);
    }
    @GetMapping("/system/questionnaire/exportAllAnswers")
    public TableDataInfo exportAllAnswers(StuInfo stuInfo) {
        List<StuInfo> answers = stuInfoService.getStudentChangeMajorInfo(stuInfo);
        return getDataTable(answers);
    }
    //查询学生提交的问卷答案
    @GetMapping("/system/questionnaire/answers2")
    public TableDataInfo getStudentQuestionnaireAnswers2(UserQuestionnaireAnswer answer) {
        startPage();
        List<UserQuestionnaireAnswer> answers = userQuestionnaireService.getStudentQuestionnaireAnswers(answer);
        return getDataTable(answers);
    }
    @GetMapping("/system/questionnaire/exportAllAnswers2")
    public TableDataInfo exportAllAnswers2(UserQuestionnaireAnswer answer) {
        List<UserQuestionnaireAnswer> answers = userQuestionnaireService.getStudentQuestionnaireAnswers(answer);
        return getDataTable(answers);
    }
    //提交问卷答案，将结果保存到数据库
    @PostMapping("/system/questionnaire/submitAnswers")
    public ResponseEntity<?> submitQuestionnaire(@RequestBody QuestionnaireAnswer answer) {
        // 检查请求体中的必要字段
        if (answer.getUserName() == null ||
                answer.getQuestionnaireId() == null ||
                answer.getChangeAdress() == null ||
                answer.getChangeMajor() == null ||
                answer.getAfterChangeMajor() == null ||
                answer.getAfterChangeAdress() == null ||
                answer.getChangeMajorType() == null) {

            System.err.println("缺少必要的参数: " + answer); // 记录缺少参数的信息
            HashMap<String,String> hashMap = new HashMap<>();
            hashMap.put("message", "缺少必要的参数");
            return ResponseEntity.badRequest().body(hashMap);
        }

        try {
            userQuestionnaireService.submitQuestionnaire(answer);
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
    @GetMapping("/system/questionnaire/getQuestionnaireTimes")
    public AjaxResult getQuestionnaireTimes(@RequestParam String questionnaireId) {
        Map<String, Object> times = userQuestionnaireService.getQuestionnaireTimes(questionnaireId);
        if (times != null) {
            return AjaxResult.success(times);
        } else {
            return AjaxResult.error("问卷不存在");
        }
    }
}
