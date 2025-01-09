package com.ruoyi.system.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface UserQuestionnaireMapper {
    @Select("SELECT COUNT(*) FROM user_questionnaire_answers WHERE user_name = #{userName} AND questionnaire_id = #{questionnaireId}")
    int countUserQuestionnaireAnswers(@Param("userName") String userName, @Param("questionnaireId") int questionnaireId);
}
