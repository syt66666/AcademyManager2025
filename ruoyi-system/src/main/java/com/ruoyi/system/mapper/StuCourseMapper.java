package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.StuCourse;

/**
 * 课程信息Mapper接口
 *
 * @author ruoyi
 * @date 2025-03-01
 */
public interface StuCourseMapper
{
    /**
     * 查询课程信息
     *
     * @param courseId 课程信息主键
     * @return 课程信息
     */
    public StuCourse selectStuCourseByCourseId(Long courseId);

    /**
     * 查询课程信息列表
     *
     * @param stuCourse 课程信息
     * @return 课程信息集合
     */
    public List<StuCourse> selectStuCourseList(StuCourse stuCourse);

    /**
     * 新增课程信息
     *
     * @param stuCourse 课程信息
     * @return 结果
     */
    public int insertStuCourse(StuCourse stuCourse);

    /**
     * 修改课程信息
     *
     * @param stuCourse 课程信息
     * @return 结果
     */
    public int updateStuCourse(StuCourse stuCourse);

    /**
     * 删除课程信息
     *
     * @param courseId 课程信息主键
     * @return 结果
     */
    public int deleteStuCourseByCourseId(Long courseId);

    /**
     * 批量删除课程信息
     *
     * @param courseIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteStuCourseByCourseIds(Long[] courseIds);
}
