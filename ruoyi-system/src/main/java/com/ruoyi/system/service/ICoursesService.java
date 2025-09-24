package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.Courses;

/**
 * 书院选课Service接口
 * 
 * @author ruoyi
 * @date 2025-09-22
 */
public interface ICoursesService 
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
     * 批量删除书院选课
     * 
     * @param courseIds 需要删除的书院选课主键集合
     * @return 结果
     */
    public int deleteCoursesByCourseIds(Long[] courseIds);

    /**
     * 删除书院选课信息
     * 
     * @param courseId 书院选课主键
     * @return 结果
     */
    public int deleteCoursesByCourseId(Long courseId);
    /**
     * 学生选课
     */
    int signUpCourse(Long courseId, String studentId);

    /**
     * 学生取消选课
     */
    int cancelSignUp(Long courseId, String studentId);

    /**
     * 检查选课状态
     */
    boolean checkBookingStatus(Long courseId, String studentId);

}
