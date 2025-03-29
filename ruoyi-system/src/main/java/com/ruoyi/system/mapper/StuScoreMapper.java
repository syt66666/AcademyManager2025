package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.StuScore;

/**
 * 成绩Mapper接口
 *
 * @author ruoyi
 * @date 2025-03-01
 */
public interface StuScoreMapper
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
     * 删除成绩
     *
     * @param scoreId 成绩主键
     * @return 结果
     */
    public int deleteStuScoreByScoreId(Long scoreId);

    /**
     * 批量删除成绩
     *
     * @param scoreIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteStuScoreByScoreIds(Long[] scoreIds);

    public List<StuScore> selectByCourseCode(String courseCode);

    void batchUpdateRank(List<StuScore> allScores);
}
