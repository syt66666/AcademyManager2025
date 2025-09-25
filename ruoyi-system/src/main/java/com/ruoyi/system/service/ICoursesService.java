package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.Courses;

/**
 * 【请填写功能名称】Service接口
 *
 * @author ruoyi
 * @date 2025-09-25
 */
public interface ICoursesService
{
    /**
     * 查询【请填写功能名称】
     *
     * @param courseId 【请填写功能名称】主键
     * @return 【请填写功能名称】
     */
    public Courses selectCoursesByCourseId(Long courseId);

    /**
     * 查询【请填写功能名称】列表
     *
     * @param courses 【请填写功能名称】
     * @return 【请填写功能名称】集合
     */
    public List<Courses> selectCoursesList(Courses courses);

    /**
     * 新增【请填写功能名称】
     *
     * @param courses 【请填写功能名称】
     * @return 结果
     */
    public int insertCourses(Courses courses);

    /**
     * 修改【请填写功能名称】
     *
     * @param courses 【请填写功能名称】
     * @return 结果
     */
    public int updateCourses(Courses courses);

    /**
     * 批量删除【请填写功能名称】
     *
     * @param courseIds 需要删除的【请填写功能名称】主键集合
     * @return 结果
     */
    public int deleteCoursesByCourseIds(Long[] courseIds);

    /**
     * 删除【请填写功能名称】信息
     *
     * @param courseId 【请填写功能名称】主键
     * @return 结果
     */
    public int deleteCoursesByCourseId(Long courseId);
}
