package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.QuestionsMapper;
import com.ruoyi.system.domain.Questions;
import com.ruoyi.system.service.IQuestionsService;

/**
 * 试题管理Service业务层处理
 * 
 * @author ruoyi
 * @date 2024-12-27
 */
@Service
public class QuestionsServiceImpl implements IQuestionsService 
{
    @Autowired
    private QuestionsMapper questionsMapper;

    /**
     * 查询试题管理
     * 
     * @param id 试题管理主键
     * @return 试题管理
     */
    @Override
    public Questions selectQuestionsById(Long id)
    {
        return questionsMapper.selectQuestionsById(id);
    }

    /**
     * 查询试题管理列表
     * 
     * @param questions 试题管理
     * @return 试题管理
     */
    @Override
    public List<Questions> selectQuestionsList(Questions questions)
    {
        return questionsMapper.selectQuestionsList(questions);
    }

    /**
     * 新增试题管理
     * 
     * @param questions 试题管理
     * @return 结果
     */
    @Override
    public int insertQuestions(Questions questions)
    {
        return questionsMapper.insertQuestions(questions);
    }

    /**
     * 修改试题管理
     * 
     * @param questions 试题管理
     * @return 结果
     */
    @Override
    public int updateQuestions(Questions questions)
    {
        return questionsMapper.updateQuestions(questions);
    }

    /**
     * 批量删除试题管理
     * 
     * @param ids 需要删除的试题管理主键
     * @return 结果
     */
    @Override
    public int deleteQuestionsByIds(Long[] ids)
    {
        return questionsMapper.deleteQuestionsByIds(ids);
    }

    /**
     * 删除试题管理信息
     * 
     * @param id 试题管理主键
     * @return 结果
     */
    @Override
    public int deleteQuestionsById(Long id)
    {
        return questionsMapper.deleteQuestionsById(id);
    }
}
