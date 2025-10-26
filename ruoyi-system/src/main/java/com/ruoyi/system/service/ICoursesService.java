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

    /**
     * 减少课程容量
     *
     * @param courseId 课程ID
     * @param version 版本号
     * @return 结果
     */
    public int decreaseCapacity(Long courseId, Integer version);

    /**
     * 增加课程容量
     *
     * @param courseId 课程ID
     * @param version 版本号
     * @return 结果
     */
    public int increaseCapacity(Long courseId, Integer version);

    /**
     * 检查课程是否已开始选课
     *
     * @param courseId 课程ID
     * @return 是否已开始选课
     */
    public boolean isCourseSignUpStarted(Long courseId);

    /**
     * 事务性选课操作 - 解决并发问题
     *
     * @param courseId 课程ID
     * @param studentId 学生ID
     * @param version 版本号
     * @return 操作结果
     */
    public int signUpWithTransaction(Long courseId, String studentId, Integer version);

    /**
     * 事务性取消选课操作 - 解决并发问题
     *
     * @param courseId 课程ID
     * @param studentId 学生ID
     * @param version 版本号
     * @return 操作结果
     */
    public int cancelSignUpWithTransaction(Long courseId, String studentId, Integer version);

    /**
     * 检查课程名称和组织单位的唯一性
     *
     * @param courseName 课程名称
     * @param organizer 组织单位
     * @param courseId 课程ID（编辑时排除自身）
     * @return 是否唯一
     */
    boolean checkCourseUnique(String courseName, String organizer, Long courseId);
}
