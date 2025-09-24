package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.Courses;

/**
 * 书院选课Mapper接口
 * 
 * @author ruoyi
 * @date 2025-09-22
 */
public interface CoursesMapper 
{
    /**
     * 查询书院选课
     * 
     * @param courseId 书院选课主键
     * @return 书院选课
     */
    public Courses selectCoursesByCourseId(Long courseId);

    /**
     * 查询书院选课列表
     * 
     * @param courses 书院选课
     * @return 书院选课集合
     */
    public List<Courses> selectCoursesList(Courses courses);

    /**
     * 新增书院选课
     * 
     * @param courses 书院选课
     * @return 结果
     */
    public int insertCourses(Courses courses);

    /**
     * 修改书院选课
     * 
     * @param courses 书院选课
     * @return 结果
     */
    public int updateCourses(Courses courses);

    /**
     * 删除书院选课
     * 
     * @param courseId 书院选课主键
     * @return 结果
     */
    public int deleteCoursesByCourseId(Long courseId);

    /**
     * 批量删除书院选课
     * 
     * @param courseIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteCoursesByCourseIds(Long[] courseIds);
}
