package com.ruoyi.system.service;
import com.ruoyi.system.domain.UserQuestionnaireAnswer;

import java.util.List;
import java.util.Map;

public interface IUserQuestionnaireService {

    boolean checkQuestionnaireCompleted(String userName, int questionnaireId);

    Map<String, ?> echarts2(int questionnaireId);
    List<UserQuestionnaireAnswer> getAllStudentQuestionnaireAnswers(UserQuestionnaireAnswer answer);
    List<UserQuestionnaireAnswer> getStudentQuestionnaireAnswers(UserQuestionnaireAnswer answer);

}
