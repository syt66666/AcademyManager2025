package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.Courses;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 【请填写功能名称】Mapper接口
 *
 * @author ruoyi
 * @date 2025-09-25
 */
@Mapper
public interface CoursesMapper
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
     * 删除【请填写功能名称】
     *
     * @param courseId 【请填写功能名称】主键
     * @return 结果
     */
    public int deleteCoursesByCourseId(Long courseId);

    /**
     * 批量删除【请填写功能名称】
     *
     * @param courseIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteCoursesByCourseIds(Long[] courseIds);
    int increaseCapacity(@Param("courseId") Integer courseId, @Param("version") Integer version);
    int decreaseCapacity(@Param("courseId") Integer courseId, @Param("version") Integer version);
    int checkCourseUnique(@Param("courseName") String courseName,
                            @Param("organizer") String organizer,
                            @Param("courseId") Integer courseId);}
