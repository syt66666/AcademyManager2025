package com.ruoyi.system.service;

import java.util.Map;

public interface IUserQuestionnaireService {

    boolean checkQuestionnaireCompleted(String userName, int questionnaireId);

    Map<String, ?> echarts2(int questionnaireId);

    // 你可以添加其他方法来处理 `user_questionnaire_answers` 数据
}
