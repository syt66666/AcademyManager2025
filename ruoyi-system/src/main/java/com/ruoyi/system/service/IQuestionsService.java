package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.Questions;

/**
 * 试题管理Service接口
 * 
 * @author ruoyi
 * @date 2024-12-27
 */
public interface IQuestionsService 
{
    /**
     * 查询试题管理
     * 
     * @param id 试题管理主键
     * @return 试题管理
     */
    public Questions selectQuestionsById(Long id);

    /**
     * 查询试题管理列表
     * 
     * @param questions 试题管理
     * @return 试题管理集合
     */
    public List<Questions> selectQuestionsList(Questions questions);

    /**
     * 新增试题管理
     * 
     * @param questions 试题管理
     * @return 结果
     */
    public int insertQuestions(Questions questions);

    /**
     * 修改试题管理
     * 
     * @param questions 试题管理
     * @return 结果
     */
    public int updateQuestions(Questions questions);

    /**
     * 批量删除试题管理
     * 
     * @param ids 需要删除的试题管理主键集合
     * @return 结果
     */
    public int deleteQuestionsByIds(Long[] ids);

    /**
     * 删除试题管理信息
     * 
     * @param id 试题管理主键
     * @return 结果
     */
    public int deleteQuestionsById(Long id);
}
