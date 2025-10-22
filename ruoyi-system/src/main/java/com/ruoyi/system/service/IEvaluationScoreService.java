package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.EvaluationScore;

/**
 * 问卷得分Service接口
 * 
 * @author ruoyi
 * @date 2025-10-21
 */
public interface IEvaluationScoreService 
{
    /**
     * 查询问卷得分
     * 
     * @param id 问卷得分主键
     * @return 问卷得分
     */
    public EvaluationScore selectEvaluationScoreById(Long id);

    /**
     * 查询问卷得分列表
     * 
     * @param evaluationScore 问卷得分
     * @return 问卷得分集合
     */
    public List<EvaluationScore> selectEvaluationScoreList(EvaluationScore evaluationScore);

    /**
     * 新增问卷得分
     * 
     * @param evaluationScore 问卷得分
     * @return 结果
     */
    public int insertEvaluationScore(EvaluationScore evaluationScore);

    /**
     * 修改问卷得分
     * 
     * @param evaluationScore 问卷得分
     * @return 结果
     */
    public int updateEvaluationScore(EvaluationScore evaluationScore);

    /**
     * 批量删除问卷得分
     * 
        * @param ids 需要删除的问卷得分主键集合
     * @return 结果
     */
    public int deleteEvaluationScoreByIds(Long[] ids);

    /**
     * 删除问卷得分信息
     * 
     * @param id 问卷得分主键
     * @return 结果
     */
    public int deleteEvaluationScoreById(Long id);
}
