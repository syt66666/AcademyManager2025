package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.StuScore;

/**
 * 【请填写功能名称】Service接口
 *
 * @author ruoyi
 * @date 2025-03-01
 */
public interface IStuScoreService
{
    /**
     * 查询【请填写功能名称】
     *
     * @param scoreId 【请填写功能名称】主键
     * @return 【请填写功能名称】
     */
    public StuScore selectStuScoreByScoreId(Long scoreId);

    /**
     * 查询【请填写功能名称】列表
     *
     * @param stuScore 【请填写功能名称】
     * @return 【请填写功能名称】集合
     */
    public List<StuScore> selectStuScoreList(StuScore stuScore);

    /**
     * 新增【请填写功能名称】
     *
     * @param stuScore 【请填写功能名称】
     * @return 结果
     */
    public int insertStuScore(StuScore stuScore);

    /**
     * 修改【请填写功能名称】
     *
     * @param stuScore 【请填写功能名称】
     * @return 结果
     */
    public int updateStuScore(StuScore stuScore);

    /**
     * 批量删除【请填写功能名称】
     *
     * @param scoreIds 需要删除的【请填写功能名称】主键集合
     * @return 结果
     */
    public int deleteStuScoreByScoreIds(Long[] scoreIds);

    /**
     * 删除【请填写功能名称】信息
     *
     * @param scoreId 【请填写功能名称】主键
     * @return 结果
     */
    public int deleteStuScoreByScoreId(Long scoreId);
}
