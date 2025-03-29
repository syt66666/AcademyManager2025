package com.ruoyi.system.service.impl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.bean.BeanValidators;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.StuScoreMapper;
import com.ruoyi.system.domain.StuScore;
import com.ruoyi.system.service.IStuScoreService;

import javax.validation.Validator;

/**
 * 成绩Service业务层处理
 *
 * @author ruoyi
 * @date 2025-03-01
 */
@Service
public class StuScoreServiceImpl implements IStuScoreService
{
    @Autowired
    private StuScoreMapper stuScoreMapper;
    @Autowired
    private Validator validator;
    /**
     * 查询成绩
     *
     * @param scoreId 成绩主键
     * @return 成绩
     */
    @Override
    public StuScore selectStuScoreByScoreId(Long scoreId)
    {
        return stuScoreMapper.selectStuScoreByScoreId(scoreId);
    }

    /**
     * 查询成绩列表（增强版：动态计算百分比）
     */
    @Override
    public List<StuScore> selectStuScoreList(StuScore stuScore) {
        // 1. 获取基础数据
        List<StuScore> list = stuScoreMapper.selectStuScoreList(stuScore);

        // 2. 动态计算百分比
        list.forEach(score -> {
            if (score.getTotalStudents() != null && score.getCourseRank() != null) {
                double percent = (score.getTotalStudents() - score.getCourseRank()) * 100.0 / score.getTotalStudents();
                score.setPercentRank(Math.round(percent * 100.0) / 100.0); // 保留两位小数
            }
        });

        return list;
    }

    /**
     * 新增成绩
     *
     * @param stuScore 成绩
     * @return 结果
     */
    @Override
    public int insertStuScore(StuScore stuScore)
    {
        return stuScoreMapper.insertStuScore(stuScore);
    }

    /**
     * 修改成绩
     *
     * @param stuScore 成绩
     * @return 结果
     */
    @Override
    public int updateStuScore(StuScore stuScore)
    {
        return stuScoreMapper.updateStuScore(stuScore);
    }

    /**
     * 批量删除成绩
     *
     * @param scoreIds 需要删除的成绩主键
     * @return 结果
     */
    @Override
    public int deleteStuScoreByScoreIds(Long[] scoreIds)
    {
        return stuScoreMapper.deleteStuScoreByScoreIds(scoreIds);
    }

    /**
     * 删除成绩信息
     *
     * @param scoreId 成绩主键
     * @return 结果
     */
    @Override
    public int deleteStuScoreByScoreId(Long scoreId)
    {
        return stuScoreMapper.deleteStuScoreByScoreId(scoreId);
    }

    /**
     * 导入用户数据
     *
     * @param importList 用户数据列表
     * @param updateSupport 是否更新支持，如果已存在，则进行更新数据
     * @param operName 操作用户
     * @return 结果
     */
    @Override
    public String importScore(List<StuScore> importList, boolean updateSupport, String operName) {
        if (CollectionUtils.isEmpty(importList)) {
            throw new ServiceException("导入数据不能为空");
        }

        // 1. 按课程分组处理
        Map<String, List<StuScore>> courseMap = importList.stream()
                .collect(Collectors.groupingBy(StuScore::getCourseCode));

        courseMap.forEach((courseCode, scores) -> {
            // 2. 保存/更新原始数据
            scores.forEach(score -> {
                if (updateSupport) {
                    stuScoreMapper.updateStuScore(score);
                } else {
                    stuScoreMapper.insertStuScore(score);
                }
            });

            // 3. 获取课程所有成绩（包含新导入的）
            List<StuScore> allScores = stuScoreMapper.selectByCourseCode(courseCode);

            // 4. 计算排名和总人数
            calculateRank(allScores); // 只计算排名和总人数

            // 5. 批量更新排名和总人数到数据库
            stuScoreMapper.batchUpdateRank(allScores);
        });

        return "成功导入" + importList.size() + "条，影响课程：" + courseMap.keySet();
    }

    // 仅计算排名和总人数
    private void calculateRank(List<StuScore> scores) {
        // 按成绩降序排序
        scores.sort((s1, s2) -> Integer.compare(
                Integer.parseInt(s2.getScoreValue()),
                Integer.parseInt(s1.getScoreValue())
        ));

        int rank = 1;
        int prevScore = -1;
        for (int i = 0; i < scores.size(); i++) {
            StuScore score = scores.get(i);
            int currentScore = Integer.parseInt(score.getScoreValue());

            if (currentScore != prevScore) {
                rank = i + 1;
            }
            score.setCourseRank(rank);
            prevScore = currentScore;
        }

        // 设置总人数（不设置百分比）
        int total = scores.size();
        scores.forEach(score -> score.setTotalStudents(total));
    }

}
