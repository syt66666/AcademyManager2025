package com.ruoyi.system.service;

public interface IUserQuestionnaireService {

    boolean checkQuestionnaireCompleted(String userName, int questionnaireId);

    // 你可以添加其他方法来处理 `user_questionnaire_answers` 数据
}
