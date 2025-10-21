package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.EvaluationScoreMapper;
import com.ruoyi.system.domain.EvaluationScore;
import com.ruoyi.system.service.IEvaluationScoreService;

/**
 * 问卷得分Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-10-21
 */
@Service
public class EvaluationScoreServiceImpl implements IEvaluationScoreService 
{
    @Autowired
    private EvaluationScoreMapper evaluationScoreMapper;

    /**
     * 查询问卷得分
     * 
     * @param id 问卷得分主键
     * @return 问卷得分
     */
    @Override
    public EvaluationScore selectEvaluationScoreById(Long id)
    {
        return evaluationScoreMapper.selectEvaluationScoreById(id);
    }

    /**
    * 查询问卷得分列表
     * 
     * @param evaluationScore 问卷得分
     * @return 问卷得分集合
     */
    @Override
    public List<EvaluationScore> selectEvaluationScoreList(EvaluationScore evaluationScore)
    {
        return evaluationScoreMapper.selectEvaluationScoreList(evaluationScore);
    }

    /**
     * 新增问卷得分
     * 
     * @param evaluationScore 问卷得分
     * @return 结果
     */
    @Override
    public int insertEvaluationScore(EvaluationScore evaluationScore)
    {
        return evaluationScoreMapper.insertEvaluationScore(evaluationScore);
    }

    /**
     * 修改问卷得分
     * 
     * @param evaluationScore 问卷得分
     * @return 结果
     */
    @Override
    public int updateEvaluationScore(EvaluationScore evaluationScore)
    {
        return evaluationScoreMapper.updateEvaluationScore(evaluationScore);
    }

    /**
     * 批量删除问卷得分
     * 
     * @param ids 需要删除的问卷得分主键集合
     * @return 结果
     */
    @Override
    public int deleteEvaluationScoreByIds(Long[] ids)
    {
        return evaluationScoreMapper.deleteEvaluationScoreByIds(ids);
    }

    /**
     * 删除问卷得分信息
     * 
     * @param id 问卷得分主键
     * @return 结果
     */
    @Override
    public int deleteEvaluationScoreById(Long id)
    {
        return evaluationScoreMapper.deleteEvaluationScoreById(id);
    }
}
