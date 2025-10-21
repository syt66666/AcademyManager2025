package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.EvaluationScore;

/**
 * 问卷得分Mapper接口
 * 
 * @author ruoyi
 * @date 2025-10-21
 */
public interface EvaluationScoreMapper 
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
     * 删除问卷得分
     * 
     * @param id 问卷得分主键
     * @return 结果
     */
    public int deleteEvaluationScoreById(Long id);

    /**
     * 批量删除问卷得分
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteEvaluationScoreByIds(Long[] ids);
}
