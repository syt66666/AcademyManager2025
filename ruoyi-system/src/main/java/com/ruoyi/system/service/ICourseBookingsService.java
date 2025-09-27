package com.ruoyi.system.service;

import java.util.List;
import java.util.Map;
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

    /**
     * 检查学生是否选课了指定课程
     *
     * @param courseId 课程ID
     * @param studentId 学生ID
     * @return 是否已选课
     */
    public boolean checkIfBooked(Long courseId, String studentId);

    /**
     * 根据课程ID和学生ID删除选课记录
     *
     * @param courseId 课程ID
     * @param studentId 学生ID
     * @return 结果
     */
    public int deleteByCourseAndStudent(Long courseId, String studentId);

    /**
     * 查询课程选课记录列表（包含详情）
     *
     * @param courseBookingDTO 课程选课记录DTO
     * @return 课程选课记录集合（包含详情）
     */
    public List<CourseBookingDTO> selectCourseBookingsListWithDetails(CourseBookingDTO courseBookingDTO);

    /**
     * 查询课程选课记录列表（审核列表）
     *
     * @param courseBookingDTO 课程选课记录DTO
     * @return 课程选课记录集合（审核列表）
     */
    public List<CourseBookingDTO> selectCourseBookingsListAudit(CourseBookingDTO courseBookingDTO);

    /**
     * 根据ID查询课程选课记录（包含详情）
     *
     * @param bookingId 课程选课记录主键
     * @return 课程选课记录（包含详情）
     */
    public CourseBookingDTO selectCourseBookingsByBookingIdWithDetails(Long bookingId);

    /**
     * 专门的审核更新方法，只更新审核相关字段
     *
     * @param courseBookings 课程选课记录
     * @return 结果
     */
    public int updateCourseBookingsAudit(CourseBookings courseBookings);

    /**
     * 统计审核状态
     *
     * @return 审核状态统计
     */
    public Map<String, Object> countCourseBookingAuditStatus();
}
