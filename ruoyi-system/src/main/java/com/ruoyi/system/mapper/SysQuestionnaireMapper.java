package com.ruoyi.system.mapper;

import org.apache.ibatis.annotations.Param;

import java.util.Map;

public interface SysQuestionnaireMapper {
    Map<String, Object> selectQuestionnaireTimesById(@Param("questionnaireId") String questionnaireId);
}

