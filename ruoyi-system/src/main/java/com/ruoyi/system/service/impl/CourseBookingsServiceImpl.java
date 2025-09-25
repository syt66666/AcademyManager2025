package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.CourseBookingsMapper;
import com.ruoyi.system.domain.CourseBookings;
import com.ruoyi.system.domain.CourseBookingDTO;
import com.ruoyi.system.service.ICourseBookingsService;

/**
 * 课程选课记录Service业务层处理
 *
 * @author ruoyi
 * @date 2025-09-25
 */
@Service
public class CourseBookingsServiceImpl implements ICourseBookingsService
{
    @Autowired
    private CourseBookingsMapper courseBookingsMapper;

    /**
     * 查询课程选课记录
     *
     * @param bookingId 课程选课记录主键
     * @return 课程选课记录
     */
    @Override
    public CourseBookings selectCourseBookingsByBookingId(Long bookingId)
    {
        return courseBookingsMapper.selectCourseBookingsByBookingId(bookingId);
    }

    /**
     * 查询课程选课记录列表
     *
     * @param courseBookings 课程选课记录
     * @return 课程选课记录
     */
    @Override
    public List<CourseBookings> selectCourseBookingsList(CourseBookings courseBookings)
    {
        return courseBookingsMapper.selectCourseBookingsList(courseBookings);
    }

    /**
     * 新增课程选课记录
     *
     * @param courseBookings 课程选课记录
     * @return 结果
     */
    @Override
    public int insertCourseBookings(CourseBookings courseBookings)
    {
        return courseBookingsMapper.insertCourseBookings(courseBookings);
    }

    /**
     * 修改课程选课记录
     *
     * @param courseBookings 课程选课记录
     * @return 结果
     */
    @Override
    public int updateCourseBookings(CourseBookings courseBookings)
    {
        return courseBookingsMapper.updateCourseBookings(courseBookings);
    }

    /**
     * 批量删除课程选课记录
     *
     * @param bookingIds 需要删除的课程选课记录主键
     * @return 结果
     */
    @Override
    public int deleteCourseBookingsByBookingIds(Long[] bookingIds)
    {
        return courseBookingsMapper.deleteCourseBookingsByBookingIds(bookingIds);
    }

    /**
     * 删除课程选课记录信息
     *
     * @param bookingId 课程选课记录主键
     * @return 结果
     */
    @Override
    public int deleteCourseBookingsByBookingId(Long bookingId)
    {
        return courseBookingsMapper.deleteCourseBookingsByBookingId(bookingId);
    }

    /**
     * 查询课程选课记录列表（包含课程信息）
     *
     * @param courseBookings 课程选课记录
     * @return 课程选课记录集合（包含课程信息）
     */
    @Override
    public List<CourseBookingDTO> selectCourseBookingsWithCourseList(CourseBookings courseBookings)
    {
        return courseBookingsMapper.selectCourseBookingsWithCourseList(courseBookings);
    }
}
