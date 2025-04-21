package com.ruoyi.system.service.impl;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.QuestionnaireAnswer;
import com.ruoyi.system.domain.UserQuestionnaireAnswer;
import com.ruoyi.system.mapper.UserQuestionnaireMapper;
import com.ruoyi.system.service.IUserQuestionnaireService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class UserQuestionnaireServiceImpl implements IUserQuestionnaireService {

    private static final Logger log = LoggerFactory.getLogger(UserQuestionnaireServiceImpl.class);
    @Autowired
    private UserQuestionnaireMapper userQuestionnaireMapper;

    @Override
    public boolean checkQuestionnaireCompleted(String userName, int questionnaireId) {
        int count = userQuestionnaireMapper.countUserQuestionnaireAnswers(userName, questionnaireId);
        return count > 0;
    }

    @Override
    public Map<String, ?> echarts2(int questionnaireId) {
        Map<String, Object> result = new HashMap<>();
        Map<String,Integer> beforeCnt = new HashMap<>();
        Map<String, Map<String, Integer>> afterCnt1 = new HashMap<>();
        Map<String, Map<String, Integer>> afterCnt2 = new HashMap<>();
        Map<String, Map<String, Map<String, Integer>>> changeMajorType = new HashMap<>();
        Map<String, Map<String, Map<String, Integer>>> afterMajorChangeType = new HashMap<>();

    List<UserQuestionnaireAnswer> answers = userQuestionnaireMapper.getQuestionnaireAnswers(questionnaireId);
    for (UserQuestionnaireAnswer answer : answers) {

        String academy = answer.getAcademy();
        String originalSystemMajor = answer.getOriginalSystemMajor();
        String changeAcademy = answer.getChangeAdress();
        String changeMajor = answer.getChangeMajor();
        String changeType = answer.getChangeMajorType();
        String afterChangeAcademy = answer.getAfterChangeAdress();
        String afterChangeMajor = answer.getAfterChangeMajor();
        String afterChangeType = answer.getAfterChangeMajorType();


        if (StringUtils.isNotEmpty(academy)) {
            // 获取当前学院的学生总数
            int totalCount = beforeCnt.getOrDefault(academy, 0);
            // 更新学院的学生总数
            beforeCnt.put(academy, totalCount + 1);
        }

        // 处理分流后一阶段数据
        if (StringUtils.isNotEmpty(changeAcademy) && StringUtils.isNotEmpty(changeMajor)) {
            Map<String, Integer> afterMap1 = afterCnt1.computeIfAbsent(changeAcademy, k -> new HashMap<>());
            afterMap1.put(changeMajor, afterMap1.getOrDefault(changeMajor, 0) + 1);

            Map<String, Map<String, Integer>> academyChangeType = changeMajorType.computeIfAbsent(changeAcademy, k -> new HashMap<>());
            Map<String, Integer> majorChangeType1 = academyChangeType.computeIfAbsent(changeMajor, k -> new HashMap<>());
            majorChangeType1.put(changeType, majorChangeType1.getOrDefault(changeType, 0) + 1);
        }

        // 处理转专业后二阶段数据
        if (StringUtils.isNotEmpty(afterChangeAcademy) && StringUtils.isNotEmpty(afterChangeMajor)) {
            Map<String, Integer> afterMap2 = afterCnt2.computeIfAbsent(afterChangeAcademy, k -> new HashMap<>());
            afterMap2.put(afterChangeMajor, afterMap2.getOrDefault(afterChangeMajor, 0) + 1);

            Map<String, Map<String, Integer>> academyChangeType = afterMajorChangeType.computeIfAbsent(afterChangeAcademy, k -> new HashMap<>());
            Map<String, Integer> majorChangeType = academyChangeType.computeIfAbsent(afterChangeMajor, k -> new HashMap<>());
            majorChangeType.put(afterChangeType, majorChangeType.getOrDefault(afterChangeType, 0) + 1);
        }
    }
    result.put("beforeCnt", beforeCnt);
    result.put("afterCnt1", afterCnt1);
    result.put("afterCnt2", afterCnt2);
    result.put("changeMajorType", changeMajorType);
    result.put("afterMajorChangeType", afterMajorChangeType);
    return result;
}

    @Override
    public List<UserQuestionnaireAnswer> getAllStudentQuestionnaireAnswers(UserQuestionnaireAnswer answer) {
        return userQuestionnaireMapper.getAllStudentQuestionnaireAnswers(answer);
    }

    @Override
    public List<UserQuestionnaireAnswer> getStudentQuestionnaireAnswers(UserQuestionnaireAnswer answer) {
        return userQuestionnaireMapper.getStudentQuestionnaireAnswers(answer);
    }

        @Override
        public void submitQuestionnaire(QuestionnaireAnswer answer) {
            userQuestionnaireMapper.insertQuestionnaireAnswer(
                    answer.getUserName(),
                    answer.getQuestionnaireId(),

                    answer.getChangeAdress(),
                    answer.getChangeMajor(),
                    answer.getChangeMajorType(),

                    answer.getAfterChangeAdress(),
                    answer.getAfterChangeMajor(),
                    answer.getAfterChangeMajorType()

            );
        }
    @Override
    public Map<String, Object> getQuestionnaireTimes(String questionnaireId) {
        return userQuestionnaireMapper.selectQuestionnaireTimesById(questionnaireId);
    }
}

