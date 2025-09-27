package com.ruoyi.system.service.impl;

import java.util.List;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.system.domain.Courses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.CoursesMapper;
import com.ruoyi.system.domain.Courses;
import com.ruoyi.system.service.ICoursesService;

/**
 * 【请填写功能名称】Service业务层处理
 *
 * @author ruoyi
 * @date 2025-09-25
 */
@Service
public class CoursesServiceImpl implements ICoursesService
{
    @Autowired
    private CoursesMapper coursesMapper;

    /**
     * 查询【请填写功能名称】
     *
     * @param courseId 【请填写功能名称】主键
     * @return 【请填写功能名称】
     */
    @Override
    public Courses selectCoursesByCourseId(Long courseId)
    {
        return coursesMapper.selectCoursesByCourseId(courseId);
    }

    /**
     * 查询【请填写功能名称】列表
     *
     * @param courses 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    @Override
    public List<Courses> selectCoursesList(Courses courses)
    {
        return coursesMapper.selectCoursesList(courses);
    }

    /**
     * 新增【请填写功能名称】
     *
     * @param courses 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int insertCourses(Courses courses)
    {
        return coursesMapper.insertCourses(courses);
    }

    /**
     * 修改【请填写功能名称】
     *
     * @param courses 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int updateCourses(Courses courses)
    {
        return coursesMapper.updateCourses(courses);
    }

    /**
     * 批量删除【请填写功能名称】
     *
     * @param courseIds 需要删除的【请填写功能名称】主键
     * @return 结果
     */
    @Override
    public int deleteCoursesByCourseIds(Long[] courseIds)
    {
        return coursesMapper.deleteCoursesByCourseIds(courseIds);
    }

    /**
     * 删除【请填写功能名称】信息
     *
     * @param courseId 【请填写功能名称】主键
     * @return 结果
     */
    @Override
    public int deleteCoursesByCourseId(Long courseId)
    {
        return coursesMapper.deleteCoursesByCourseId(courseId);
    }
    /**
     * 修改活动容量
     */
    @Override
    public int increaseCapacity(Integer courseId,Integer version) {
        Courses course = coursesMapper.selectCoursesByCourseId((long)courseId);
        if (course == null) {
            throw new ServiceException("活动不存在！");
        }
        int result = coursesMapper.increaseCapacity(courseId, version);
        if (result == 0) {
            throw new ServiceException("数据出现错误，请刷新重试！");
        }
        return result;
    }

    @Override
    public int decreaseCapacity(Integer courseId,Integer version) {
        Courses course = coursesMapper.selectCoursesByCourseId((long)courseId);
        if (course == null) {
            throw new ServiceException("活动不存在！");
        }
        int result = coursesMapper.decreaseCapacity(courseId, version);
        if (result == 0) {
            throw new ServiceException("数据出现错误，请刷新重试！");
        }
        return result;
    }

    @Override
    public boolean checkCourseUnique(String courseName, String organizer, Integer courseId) {
        int count = coursesMapper.checkCourseUnique(courseName, organizer, courseId);
        return count == 0;
    }

}
