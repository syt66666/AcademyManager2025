package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.StuScoreMapper;
import com.ruoyi.system.domain.StuScore;
import com.ruoyi.system.service.IStuScoreService;

/**
 * 【请填写功能名称】Service业务层处理
 *
 * @author ruoyi
 * @date 2025-03-01
 */
@Service
public class StuScoreServiceImpl implements IStuScoreService
{
    @Autowired
    private StuScoreMapper stuScoreMapper;

    /**
     * 查询【请填写功能名称】
     *
     * @param scoreId 【请填写功能名称】主键
     * @return 【请填写功能名称】
     */
    @Override
    public StuScore selectStuScoreByScoreId(Long scoreId)
    {
        return stuScoreMapper.selectStuScoreByScoreId(scoreId);
    }

    /**
     * 查询【请填写功能名称】列表
     *
     * @param stuScore 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    @Override
    public List<StuScore> selectStuScoreList(StuScore stuScore)
    {
        return stuScoreMapper.selectStuScoreList(stuScore);
    }

    /**
     * 新增【请填写功能名称】
     *
     * @param stuScore 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int insertStuScore(StuScore stuScore)
    {
        return stuScoreMapper.insertStuScore(stuScore);
    }

    /**
     * 修改【请填写功能名称】
     *
     * @param stuScore 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int updateStuScore(StuScore stuScore)
    {
        return stuScoreMapper.updateStuScore(stuScore);
    }

    /**
     * 批量删除【请填写功能名称】
     *
     * @param scoreIds 需要删除的【请填写功能名称】主键
     * @return 结果
     */
    @Override
    public int deleteStuScoreByScoreIds(Long[] scoreIds)
    {
        return stuScoreMapper.deleteStuScoreByScoreIds(scoreIds);
    }

    /**
     * 删除【请填写功能名称】信息
     *
     * @param scoreId 【请填写功能名称】主键
     * @return 结果
     */
    @Override
    public int deleteStuScoreByScoreId(Long scoreId)
    {
        return stuScoreMapper.deleteStuScoreByScoreId(scoreId);
    }
}
