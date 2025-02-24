package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.UserQuestionnaireAnswer;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

public interface UserQuestionnaireMapper {
    @Select("SELECT COUNT(*) FROM user_questionnaire_answers WHERE user_name = #{userName} AND questionnaire_id = #{questionnaireId}")
    int countUserQuestionnaireAnswers(@Param("userName") String userName, @Param("questionnaireId") int questionnaireId);

    @Select("SELECT * FROM user_questionnaire_answers u join student s on s.studentId = u.user_name WHERE questionnaire_id = #{questionnaireId}")
    @Results({
            //@Result(column = "studentId", property = "studentId"),
            @Result(column = "academy", property = "academy"),
            @Result(column = "systemMajor", property = "systemMajor"),

            @Result(column = "change_adress", property = "changeAdress"),
            @Result(column = "change_major", property = "changeMajor"),
            @Result(column = "change_major_type", property = "changeMajorType"),
            @Result(column = "after_change_adress", property = "afterChangeAdress"),
            @Result(column = "after_change_major", property = "afterChangeMajor"),
            @Result(column = "after_change_major_type", property = "afterChangeMajorType")
    })
    List<UserQuestionnaireAnswer> getQuestionnaireAnswers(@Param("questionnaireId") int questionnaireId);

    List<UserQuestionnaireAnswer> getStudentQuestionnaireAnswers(UserQuestionnaireAnswer answer);
    List<UserQuestionnaireAnswer> getAllStudentQuestionnaireAnswers(UserQuestionnaireAnswer answer);

    void insertQuestionnaireAnswer(@Param("userName") String userName,
                                   @Param("questionnaireId") int questionnaireId,
                                   @Param("changeAdress") String changeAdress,
                                   @Param("changeMajor") String changeMajor,
                                   @Param("changeMajorType") String changeMajorType,
                                   @Param("afterChangeAdress") String afterChangeAdress,
                                   @Param("afterChangeMajor") String afterChangeMajor,
                                   @Param("afterChangeMajorType") String afterChangeMajorType);

    Map<String, Object> selectQuestionnaireTimesById(String questionnaireId);
}
