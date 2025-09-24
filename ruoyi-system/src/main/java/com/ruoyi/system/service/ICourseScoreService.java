package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.CourseScore;

/**
 * 课程成绩Service接口
 * 
 * @author ruoyi
 * @date 2025-09-24
 */
public interface ICourseScoreService 
{
    /**
     * 查询课程成绩
     * 
     * @param scoreId 课程成绩主键
     * @return 课程成绩
     */
    public CourseScore selectCourseScoreByScoreId(Long scoreId);

    /**
     * 查询课程成绩列表
     * 
     * @param courseScore 课程成绩
     * @return 课程成绩集合
     */
    public List<CourseScore> selectCourseScoreList(CourseScore courseScore);

    /**
     * 新增课程成绩
     * 
     * @param courseScore 课程成绩
     * @return 结果
     */
    public int insertCourseScore(CourseScore courseScore);

    /**
     * 修改课程成绩
     * 
     * @param courseScore 课程成绩
     * @return 结果
     */
    public int updateCourseScore(CourseScore courseScore);

    /**
     * 批量删除课程成绩
     * 
     * @param scoreIds 需要删除的课程成绩主键集合
     * @return 结果
     */
    public int deleteCourseScoreByScoreIds(Long[] scoreIds);

    /**
     * 删除课程成绩信息
     * 
     * @param scoreId 课程成绩主键
     * @return 结果
     */
    public int deleteCourseScoreByScoreId(Long scoreId);
}

