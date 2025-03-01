package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.StuCourseMapper;
import com.ruoyi.system.domain.StuCourse;
import com.ruoyi.system.service.IStuCourseService;

/**
 * 课程信息Service业务层处理
 *
 * @author ruoyi
 * @date 2025-03-01
 */
@Service
public class StuCourseServiceImpl implements IStuCourseService
{
    @Autowired
    private StuCourseMapper stuCourseMapper;

    /**
     * 查询课程信息
     *
     * @param courseId 课程信息主键
     * @return 课程信息
     */
    @Override
    public StuCourse selectStuCourseByCourseId(Long courseId)
    {
        return stuCourseMapper.selectStuCourseByCourseId(courseId);
    }

    /**
     * 查询课程信息列表
     *
     * @param stuCourse 课程信息
     * @return 课程信息
     */
    @Override
    public List<StuCourse> selectStuCourseList(StuCourse stuCourse)
    {
        return stuCourseMapper.selectStuCourseList(stuCourse);
    }

    /**
     * 新增课程信息
     *
     * @param stuCourse 课程信息
     * @return 结果
     */
    @Override
    public int insertStuCourse(StuCourse stuCourse)
    {
        return stuCourseMapper.insertStuCourse(stuCourse);
    }

    /**
     * 修改课程信息
     *
     * @param stuCourse 课程信息
     * @return 结果
     */
    @Override
    public int updateStuCourse(StuCourse stuCourse)
    {
        return stuCourseMapper.updateStuCourse(stuCourse);
    }

    /**
     * 批量删除课程信息
     *
     * @param courseIds 需要删除的课程信息主键
     * @return 结果
     */
    @Override
    public int deleteStuCourseByCourseIds(Long[] courseIds)
    {
        return stuCourseMapper.deleteStuCourseByCourseIds(courseIds);
    }

    /**
     * 删除课程信息信息
     *
     * @param courseId 课程信息主键
     * @return 结果
     */
    @Override
    public int deleteStuCourseByCourseId(Long courseId)
    {
        return stuCourseMapper.deleteStuCourseByCourseId(courseId);
    }
}
