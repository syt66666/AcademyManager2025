package com.ruoyi.system.mapper;

import java.util.List;
import java.util.Map;

import com.ruoyi.system.domain.CourseBookings;
import com.ruoyi.system.domain.dto.BookingDTO;
import com.ruoyi.system.domain.dto.BookingExportDTO;
import com.ruoyi.system.domain.dto.CourseBookingDTO;
import com.ruoyi.system.domain.dto.CourseBookingExportDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 课程选课记录Mapper接口
 *
 * @author ruoyi
 * @date 2025-09-25
 */
@Mapper
public interface CourseBookingsMapper
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
     * 删除课程选课记录
     *
     * @param bookingId 课程选课记录主键
     * @return 结果
     */
    public int deleteCourseBookingsByBookingId(Long bookingId);

    /**
     * 批量删除课程选课记录
     *
     * @param bookingIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteCourseBookingsByBookingIds(Long[] bookingIds);
    /**
     * 审核更新预约（只更新审核相关字段，不影响文件字段）
     * @param bookings 预约对象
     * @return 修改结果
     */
    int updateCourseBookingsAudit(CourseBookings bookings);
    int checkCourseBookingExists(@Param("courseId") Long courseId,
                           @Param("studentId") String studentId);
    /**
     * 根据课程ID和学生ID删除预约记录
     * @param courseId 活动ID
     * @param studentId 学生ID
     * @return 删除结果
     */
    int deleteCourseBookingsByCourseAndStudent(@Param("courseId") Long courseId,
                                           @Param("studentId") String studentId);

    List<BookingDTO> selectCourseBookingsList2(BookingDTO bookings);

    List<BookingDTO> selectCourseBookingsList3(BookingDTO bookings);

    Map<String, Integer> countAuditStatus();

    /**
     * 检查学生是否预约了指定活动
     * @param courseId 活动ID
     * @param studentId 学生ID
     * @return 是否已预约
     */
    boolean checkIfBooked(@Param("courseId") Long courseId,
                          @Param("studentId") String studentId);

    List<Long> selectCourseIdsByStudentId(@Param("studentId") String studentId);

    /**
     * 查询预约学生导出数据
     * @param bookingExportDTO 查询条件
     * @return 导出数据列表
     */
    List<BookingExportDTO> selectCourseBookingExportList(BookingExportDTO bookingExportDTO);

    /**
     * 查询课程选课记录列表（包含详情）
     * @param courseBookingDTO 查询条件
     * @return 课程选课记录集合
     */
    List<CourseBookingDTO> selectCourseBookingsListWithDetails(CourseBookingDTO courseBookingDTO);

    /**
     * 查询课程选课记录列表（已提交状态）
     * @param courseBookingDTO 查询条件
     * @return 课程选课记录集合
     */
    List<CourseBookingDTO> selectCourseBookingsListSubmitted(CourseBookingDTO courseBookingDTO);

    /**
     * 统计课程选课审核状态
     * @return 审核状态统计
     */
    Map<String, Integer> countCourseBookingAuditStatus();

    /**
     * 检查是否已选课
     * @param courseId 课程ID
     * @param studentId 学生ID
     * @return 是否已选课
     */
    boolean checkIfCourseBooked(@Param("courseId") Long courseId, @Param("studentId") String studentId);

    /**
     * 查询课程选课学生导出数据
     * @param courseBookingExportDTO 查询条件
     * @return 导出数据列表
     */
    List<CourseBookingExportDTO> selectCourseBookingExportList(CourseBookingExportDTO courseBookingExportDTO);

    /**
     * 检查课程选课记录是否存在（简单版本）
     * @param courseId 课程ID
     * @param studentId 学生ID
     * @return 检查结果
     */
    Map<String, Object> checkCourseBookingSimple(@Param("courseId") Long courseId, @Param("studentId") String studentId);
}
