package com.ruoyi.system.service.impl;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.system.mapper.CoursesMapper;
import com.ruoyi.system.mapper.CourseBookingsMapper;
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

    @Autowired
    private CourseBookingsMapper courseBookingsMapper;

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
        // 检查课程名称和组织单位的唯一性
        if (!checkCourseUnique(courses.getCourseName(), courses.getOrganizer(), null)) {
            throw new ServiceException("课程名称和组织单位组合已存在，不能重复添加！");
        }
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

    /**
     * 事务性选课操作 - 解决并发问题
     *
     * @param courseId 课程ID
     * @param studentId 学生ID
     * @param version 版本号
     * @return 操作结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int signUpWithTransaction(Long courseId, String studentId, Integer version) {
        try {
            // 1. 原子性插入选课记录（包含重复检查和容量检查）
            int insertResult = courseBookingsMapper.signUpWithTransaction(courseId, studentId, version);
            
            if (insertResult <= 0) {
                // 插入失败，可能是重复选课或容量不足
                return 0;
            }
            
            // 2. 原子性更新课程容量
            int updateResult = courseBookingsMapper.updateCapacityAfterSignUp(courseId, version);
            
            if (updateResult <= 0) {
                // 容量更新失败，抛出异常触发回滚
                throw new ServiceException("课程容量更新失败");
            }
            
            return 1; // 成功
        } catch (Exception e) {
            System.err.println("事务性选课失败: " + e.getMessage());
            throw new ServiceException("选课失败: " + e.getMessage());
        }
    }

    /**
     * 事务性取消选课操作 - 解决并发问题
     *
     * @param courseId 课程ID
     * @param studentId 学生ID
     * @param version 版本号
     * @return 操作结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int cancelSignUpWithTransaction(Long courseId, String studentId, Integer version) {
        try {
            // 1. 原子性删除选课记录（包含版本检查）
            int deleteResult = courseBookingsMapper.cancelSignUpWithTransaction(courseId, studentId, version);
            
            if (deleteResult <= 0) {
                // 删除失败，可能是未选课或版本不匹配
                return 0;
            }
            
            // 2. 原子性更新课程容量
            int updateResult = courseBookingsMapper.updateCapacityAfterCancelSignUp(courseId, version);
            
            if (updateResult <= 0) {
                // 容量更新失败，抛出异常触发回滚
                throw new ServiceException("课程容量更新失败");
            }
            
            return 1; // 成功
        } catch (Exception e) {
            System.err.println("事务性取消选课失败: " + e.getMessage());
            throw new ServiceException("取消选课失败: " + e.getMessage());
        }
    }

    /**
     * 检查课程名称和组织单位的唯一性
     *
     * @param courseName 课程名称
     * @param organizer 组织单位
     * @param courseId 课程ID（编辑时排除自身）
     * @return 是否唯一
     */
    @Override
    public boolean checkCourseUnique(String courseName, String organizer, Long courseId) {
        int count = coursesMapper.checkCourseUnique(courseName, organizer, courseId);
        return count == 0;
    }
}
