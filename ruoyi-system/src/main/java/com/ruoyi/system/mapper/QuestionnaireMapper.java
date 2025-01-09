package com.ruoyi.system.mapper;

import org.apache.ibatis.annotations.Param;

public interface QuestionnaireMapper {
    int insertQuestionnaireAnswer(
            @Param("userName") String userName,
            @Param("questionnaireId") Long questionnaireId,
            @Param("changeAdress") String changeAdress,
            @Param("changeMajor") String changeMajor,
            @Param("afterChangeAdress") String afterChangeAdress,
            @Param("afterChangeMajor") String afterChangeMajor,
            @Param("changeMajorType") String changeMajorType
    );
}

