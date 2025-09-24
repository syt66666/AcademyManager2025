package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.CourseScoreMapper;
import com.ruoyi.system.domain.CourseScore;
import com.ruoyi.system.service.ICourseScoreService;

/**
 * 课程成绩Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-09-24
 */
@Service
public class CourseScoreServiceImpl implements ICourseScoreService 
{
    @Autowired
    private CourseScoreMapper courseScoreMapper;

    /**
     * 查询课程成绩
     * 
     * @param scoreId 课程成绩主键
     * @return 课程成绩
     */
    @Override
    public CourseScore selectCourseScoreByScoreId(Long scoreId)
    {
        return courseScoreMapper.selectCourseScoreByScoreId(scoreId);
    }

    /**
     * 查询课程成绩列表
     * 
     * @param courseScore 课程成绩
     * @return 课程成绩
     */
    @Override
    public List<CourseScore> selectCourseScoreList(CourseScore courseScore)
    {
        return courseScoreMapper.selectCourseScoreList(courseScore);
    }

    /**
     * 新增课程成绩
     * 
     * @param courseScore 课程成绩
     * @return 结果
     */
    @Override
    public int insertCourseScore(CourseScore courseScore)
    {
        return courseScoreMapper.insertCourseScore(courseScore);
    }

    /**
     * 修改课程成绩
     * 
     * @param courseScore 课程成绩
     * @return 结果
     */
    @Override
    public int updateCourseScore(CourseScore courseScore)
    {
        return courseScoreMapper.updateCourseScore(courseScore);
    }

    /**
     * 批量删除课程成绩
     * 
     * @param scoreIds 需要删除的课程成绩主键
     * @return 结果
     */
    @Override
    public int deleteCourseScoreByScoreIds(Long[] scoreIds)
    {
        return courseScoreMapper.deleteCourseScoreByScoreIds(scoreIds);
    }

    /**
     * 删除课程成绩信息
     * 
     * @param scoreId 课程成绩主键
     * @return 结果
     */
    @Override
    public int deleteCourseScoreByScoreId(Long scoreId)
    {
        return courseScoreMapper.deleteCourseScoreByScoreId(scoreId);
    }
}

