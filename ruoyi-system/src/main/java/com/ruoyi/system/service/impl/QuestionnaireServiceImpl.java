package com.ruoyi.system.service.impl;

import com.ruoyi.system.domain.QuestionnaireSubmitRequest;
import com.ruoyi.system.mapper.QuestionnaireMapper;
import com.ruoyi.system.service.QuestionnaireService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QuestionnaireServiceImpl implements QuestionnaireService {

    @Autowired
    private QuestionnaireMapper questionnaireMapper;

    @Override
    public void submitQuestionnaire(QuestionnaireSubmitRequest request) {
        questionnaireMapper.insertQuestionnaireAnswer(
                request.getUserName(),
                request.getQuestionnaireId(),
                request.getChangeAdress(),
                request.getChangeMajor(),
                request.getAfterChangeAdress(),
                request.getAfterChangeMajor(),
                request.getChangeMajorType()
        );
    }
}

