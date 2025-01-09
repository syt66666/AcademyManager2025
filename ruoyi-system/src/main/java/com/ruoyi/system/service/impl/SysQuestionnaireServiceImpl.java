package com.ruoyi.system.service.impl;

import com.ruoyi.system.mapper.SysQuestionnaireMapper;
import com.ruoyi.system.service.ISysQuestionnaireService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class SysQuestionnaireServiceImpl implements ISysQuestionnaireService {

    @Autowired
    private SysQuestionnaireMapper questionnaireMapper;

    @Override
    public Map<String, Object> getQuestionnaireTimes(String questionnaireId) {
        return questionnaireMapper.selectQuestionnaireTimesById(questionnaireId);
    }
}

