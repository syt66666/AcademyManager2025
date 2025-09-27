package com.ruoyi.system.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.CourseBookingsMapper;
import com.ruoyi.system.domain.CourseBookings;
import com.ruoyi.system.domain.dto.CourseBookingDTO;
import com.ruoyi.system.domain.dto.CourseBookingExportDTO;
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
     * 查询课程选课记录列表（包含详情）
     *
     * @param courseBookingDTO 查询条件
     * @return 课程选课记录集合
     */
    @Override
    public List<CourseBookingDTO> selectCourseBookingsListWithDetails(CourseBookingDTO courseBookingDTO)
    {
        return courseBookingsMapper.selectCourseBookingsListWithDetails(courseBookingDTO);
    }

    /**
     * 查询课程选课记录列表（已提交状态）
     *
     * @param courseBookingDTO 查询条件
     * @return 课程选课记录集合
     */
    @Override
    public List<CourseBookingDTO> selectCourseBookingsListSubmitted(CourseBookingDTO courseBookingDTO)
    {
        return courseBookingsMapper.selectCourseBookingsListSubmitted(courseBookingDTO);
    }

    /**
     * 审核更新课程选课记录
     *
     * @param courseBookings 课程选课记录
     * @return 结果
     */
    @Override
    public int updateCourseBookingsAudit(CourseBookings courseBookings)
    {
        return courseBookingsMapper.updateCourseBookingsAudit(courseBookings);
    }

    /**
     * 检查课程选课记录是否存在
     *
     * @param courseId 课程ID
     * @param studentId 学生ID
     * @return 结果
     */
    @Override
    public int checkCourseBookingExists(Long courseId, String studentId)
    {
        return courseBookingsMapper.checkCourseBookingExists(courseId, studentId);
    }

    /**
     * 根据课程ID和学生ID删除选课记录
     *
     * @param courseId 课程ID
     * @param studentId 学生ID
     * @return 结果
     */
    @Override
    public int deleteCourseBookingsByCourseAndStudent(Long courseId, String studentId)
    {
        return courseBookingsMapper.deleteCourseBookingsByCourseAndStudent(courseId, studentId);
    }

    /**
     * 统计课程选课审核状态
     *
     * @return 审核状态统计
     */
    @Override
    public Map<String, Integer> countCourseBookingAuditStatus()
    {
        return courseBookingsMapper.countCourseBookingAuditStatus();
    }

    /**
     * 检查是否已选课
     *
     * @param courseId 课程ID
     * @param studentId 学生ID
     * @return 是否已选课
     */
    @Override
    public boolean checkIfCourseBooked(Long courseId, String studentId)
    {
        return courseBookingsMapper.checkIfCourseBooked(courseId, studentId);
    }

    /**
     * 根据学生ID查询已选课程ID列表
     *
     * @param studentId 学生ID
     * @return 课程ID列表
     */
    @Override
    public List<Long> selectCourseIdsByStudentId(String studentId)
    {
        return courseBookingsMapper.selectCourseIdsByStudentId(studentId);
    }

    /**
     * 查询课程选课学生导出数据
     *
     * @param courseBookingExportDTO 查询条件
     * @return 导出数据列表
     */
    @Override
    public List<CourseBookingExportDTO> selectCourseBookingExportList(CourseBookingExportDTO courseBookingExportDTO)
    {
        return courseBookingsMapper.selectCourseBookingExportList(courseBookingExportDTO);
    }

    /**
     * 检查课程选课记录是否存在（简单版本）
     *
     * @param courseId 课程ID
     * @param studentId 学生ID
     * @return 检查结果
     */
    @Override
    public Map<String, Object> checkCourseBookingSimple(Long courseId, String studentId)
    {
        return courseBookingsMapper.checkCourseBookingSimple(courseId, studentId);
    }
}
