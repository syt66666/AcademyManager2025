package com.ruoyi.system.service;

import java.util.List;
import java.util.Map;
import com.ruoyi.system.domain.CourseBookings;
import com.ruoyi.system.domain.dto.CourseBookingDTO;
import com.ruoyi.system.domain.dto.CourseBookingExportDTO;

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
     * 查询课程选课记录列表（包含详情）
     *
     * @param courseBookingDTO 查询条件
     * @return 课程选课记录集合
     */
    public List<CourseBookingDTO> selectCourseBookingsListWithDetails(CourseBookingDTO courseBookingDTO);

    /**
     * 查询课程选课记录列表（已提交状态）
     *
     * @param courseBookingDTO 查询条件
     * @return 课程选课记录集合
     */
    public List<CourseBookingDTO> selectCourseBookingsListSubmitted(CourseBookingDTO courseBookingDTO);

    /**
     * 审核更新课程选课记录
     *
     * @param courseBookings 课程选课记录
     * @return 结果
     */
    public int updateCourseBookingsAudit(CourseBookings courseBookings);

    /**
     * 检查课程选课记录是否存在
     *
     * @param courseId 课程ID
     * @param studentId 学生ID
     * @return 结果
     */
    public int checkCourseBookingExists(Long courseId, String studentId);

    /**
     * 根据课程ID和学生ID删除选课记录
     *
     * @param courseId 课程ID
     * @param studentId 学生ID
     * @return 结果
     */
    public int deleteCourseBookingsByCourseAndStudent(Long courseId, String studentId);

    /**
     * 统计课程选课审核状态
     *
     * @return 审核状态统计
     */
    public Map<String, Integer> countCourseBookingAuditStatus();

    /**
     * 检查是否已选课
     *
     * @param courseId 课程ID
     * @param studentId 学生ID
     * @return 是否已选课
     */
    public boolean checkIfCourseBooked(Long courseId, String studentId);

    /**
     * 根据学生ID查询已选课程ID列表
     *
     * @param studentId 学生ID
     * @return 课程ID列表
     */
    public List<Long> selectCourseIdsByStudentId(String studentId);

    /**
     * 查询课程选课学生导出数据
     *
     * @param courseBookingExportDTO 查询条件
     * @return 导出数据列表
     */
    public List<CourseBookingExportDTO> selectCourseBookingExportList(CourseBookingExportDTO courseBookingExportDTO);

    /**
     * 检查课程选课记录是否存在（简单版本）
     *
     * @param courseId 课程ID
     * @param studentId 学生ID
     * @return 检查结果
     */
    public Map<String, Object> checkCourseBookingSimple(Long courseId, String studentId);
}
