package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.CourseBookings;
import com.ruoyi.system.domain.CourseBookingDTO;

/**
 * 课程选课记录Service接口
 *
 * @author ruoyi
 * @date 2025-09-25
 */
public interface ICourseBookingsService
{
    /**
     * 查询课程选课记录
     *
     * @param bookingId 课程选课记录主键
     * @return 课程选课记录
     */
    public CourseBookings selectCourseBookingsByBookingId(Long bookingId);

    /**
     * 查询课程选课记录列表
     *
     * @param courseBookings 课程选课记录
     * @return 课程选课记录集合
     */
    public List<CourseBookings> selectCourseBookingsList(CourseBookings courseBookings);

    /**
     * 新增课程选课记录
     *
     * @param courseBookings 课程选课记录
     * @return 结果
     */
    public int insertCourseBookings(CourseBookings courseBookings);

    /**
     * 修改课程选课记录
     *
     * @param courseBookings 课程选课记录
     * @return 结果
     */
    public int updateCourseBookings(CourseBookings courseBookings);

    /**
     * 批量删除课程选课记录
     *
     * @param bookingIds 需要删除的课程选课记录主键集合
     * @return 结果
     */
    public int deleteCourseBookingsByBookingIds(Long[] bookingIds);

    /**
     * 删除课程选课记录信息
     *
     * @param bookingId 课程选课记录主键
     * @return 结果
     */
    public int deleteCourseBookingsByBookingId(Long bookingId);

    /**
     * 查询课程选课记录列表（包含课程信息）
     *
     * @param courseBookings 课程选课记录
     * @return 课程选课记录集合（包含课程信息）
     */
    public List<CourseBookingDTO> selectCourseBookingsWithCourseList(CourseBookings courseBookings);
}
