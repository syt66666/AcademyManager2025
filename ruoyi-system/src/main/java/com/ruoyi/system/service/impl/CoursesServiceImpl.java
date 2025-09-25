package com.ruoyi.system.service.impl;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.CoursesMapper;
import com.ruoyi.system.domain.Courses;
import com.ruoyi.system.service.ICoursesService;

/**
 * 书院选课Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-09-22
 */

@Service
public class CoursesServiceImpl implements ICoursesService 
{
    @Autowired
    private CoursesMapper coursesMapper;

    /**
     * 查询书院选课
     * 
     * @param courseId 书院选课主键
     * @return 书院选课
     */
    @Override
    public Courses selectCoursesByCourseId(Long courseId)
    {
        return coursesMapper.selectCoursesByCourseId(courseId);
    }

    /**
     * 查询书院选课列表
     * 
     * @param courses 书院选课
     * @return 书院选课
     */
    @Override
    public List<Courses> selectCoursesList(Courses courses)
    {
        return coursesMapper.selectCoursesList(courses);
    }

    /**
     * 新增书院选课
     * 
     * @param courses 书院选课
     * @return 结果
     */
    @Override
    public int insertCourses(Courses courses)
    {
        return coursesMapper.insertCourses(courses);
    }

    /**
     * 修改书院选课
     * 
     * @param courses 书院选课
     * @return 结果
     */
    @Override
    public int updateCourses(Courses courses)
    {
        return coursesMapper.updateCourses(courses);
    }

    /**
     * 批量删除书院选课
     * 
     * @param courseIds 需要删除的书院选课主键
     * @return 结果
     */
    @Override
    public int deleteCoursesByCourseIds(Long[] courseIds)
    {
        return coursesMapper.deleteCoursesByCourseIds(courseIds);
    }

    /**
     * 删除书院选课信息
     * 
     * @param courseId 书院选课主键
     * @return 结果
     */
    @Override
    public int deleteCoursesByCourseId(Long courseId)
    {
        return coursesMapper.deleteCoursesByCourseId(courseId);
    }
    @Override
    public int signUpCourse(Long courseId, String studentId) {
        // 1. 检查课程是否存在且有余量
        Courses course = coursesMapper.selectCoursesByCourseId(courseId);
        if (course == null) {
            throw new RuntimeException("课程不存在");
        }
        if (course.getCourseCapacity() <= 0) {
            throw new RuntimeException("课程已满，无法选课");
        }

        // 2. 检查是否已经选过该课程
        // 这里需要根据您的数据库结构来实现检查逻辑

        // 3. 减少课程容量
        course.setCourseCapacity(course.getCourseCapacity() - 1);
        coursesMapper.updateCourses(course);

        // 4. 添加选课记录（需要创建选课表）
        // 这里需要根据您的数据库结构来实现

        return 1;
    }

    @Override
    public int cancelSignUp(Long courseId, String studentId) {
        // 1. 检查是否选过该课程

        // 2. 增加课程容量
        Courses course = coursesMapper.selectCoursesByCourseId(courseId);
        if (course != null) {
            course.setCourseCapacity(course.getCourseCapacity() + 1);
            coursesMapper.updateCourses(course);
        }

        // 3. 删除选课记录

        return 1;
    }

    @Override
    public boolean checkBookingStatus(Long courseId, String studentId) {
        // 根据课程ID和学生ID检查选课状态
        // 需要根据您的数据库结构来实现
        return false;
    }

}
