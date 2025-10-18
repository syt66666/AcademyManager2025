package com.ruoyi.system.service.impl;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.CoursesMapper;
import com.ruoyi.system.domain.Courses;
import com.ruoyi.system.service.ICoursesService;
import com.ruoyi.common.exception.ServiceException;

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
        // 检查是否有课程已开始选课
        for (Long courseId : courseIds) {
            if (isCourseSignUpStarted(courseId)) {
                Courses course = coursesMapper.selectCoursesByCourseId(courseId);
                String courseName = course != null ? course.getCourseName() : "未知课程";
                throw new ServiceException("课程【" + courseName + "】已开始选课，无法删除！");
            }
        }
        
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
        // 检查课程是否已开始选课
        if (isCourseSignUpStarted(courseId)) {
            Courses course = coursesMapper.selectCoursesByCourseId(courseId);
            String courseName = course != null ? course.getCourseName() : "未知课程";
            throw new ServiceException("课程【" + courseName + "】已开始选课，无法删除！");
        }
        
        return coursesMapper.deleteCoursesByCourseId(courseId);
    }

    /**
     * 减少课程容量
     *
     * @param courseId 课程ID
     * @param version 版本号
     * @return 结果
     */
    @Override
    public int decreaseCapacity(Long courseId, Integer version)
    {
        return coursesMapper.decreaseCapacity(courseId, version);
    }

    /**
     * 增加课程容量
     *
     * @param courseId 课程ID
     * @param version 版本号
     * @return 结果
     */
    @Override
    public int increaseCapacity(Long courseId, Integer version)
    {
        return coursesMapper.increaseCapacity(courseId, version);
    }

    /**
     * 检查课程是否已开始选课
     *
     * @param courseId 课程ID
     * @return 是否已开始选课
     */
    @Override
    public boolean isCourseSignUpStarted(Long courseId)
    {
        Courses course = coursesMapper.selectCoursesByCourseId(courseId);
        if (course == null || course.getCourseStart() == null) {
            return false;
        }
        
        Date now = new Date();
        return now.after(course.getCourseStart()) || now.equals(course.getCourseStart());
    }
}
