package com.ruoyi.system.service.impl;
import com.ruoyi.system.mapper.UserQuestionnaireMapper;
import com.ruoyi.system.service.IUserQuestionnaireService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserQuestionnaireServiceImpl implements IUserQuestionnaireService {

    @Autowired
    private UserQuestionnaireMapper userQuestionnaireMapper;

    @Override
    public boolean checkQuestionnaireCompleted(String userName, int questionnaireId) {
        int count = userQuestionnaireMapper.countUserQuestionnaireAnswers(userName, questionnaireId);
        return count > 0;
    }

    // 你可以添加其他方法来处理 `user_questionnaire_answers` 数据
}

