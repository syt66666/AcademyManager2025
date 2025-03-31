package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.StuScore;

/**
 * 成绩Service接口
 *
 * @author ruoyi
 * @date 2025-03-01
 */
public interface IStuScoreService
{
    /**
     * 查询成绩
     *
     * @param scoreId 成绩主键
     * @return 成绩
     */
    public StuScore selectStuScoreByScoreId(Long scoreId);

    /**
     * 查询成绩列表
     *
     * @param stuScore 成绩
     * @return 成绩集合
     */
    public List<StuScore> selectStuScoreList(StuScore stuScore);

    /**
     * 新增成绩
     *
     * @param stuScore 成绩
     * @return 结果
     */
    public int insertStuScore(StuScore stuScore);

    /**
     * 修改成绩
     *
     * @param stuScore 成绩
     * @return 结果
     */
    public int updateStuScore(StuScore stuScore);

    /**
     * 批量删除成绩
     *
     * @param scoreIds 需要删除的成绩主键集合
     * @return 结果
     */
    public int deleteStuScoreByScoreIds(Long[] scoreIds);

    /**
     * 删除成绩信息
     *
     * @param scoreId 成绩主键
     * @return 结果
     */
    public int deleteStuScoreByScoreId(Long scoreId);

    String importScore(List<StuScore> userList, boolean updateSupport, String operName);
}
