package com.ruoyi.system.mapper;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.system.domain.CourseBookings;
import com.ruoyi.system.domain.CourseBookingDTO;
import com.ruoyi.system.domain.dto.CourseBookingExportDTO;
import org.apache.ibatis.annotations.Mapper;

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
     * 查询课程选课记录（别名方法）
     *
     * @param bookingId 课程选课记录主键
     * @return 课程选课记录
     */
    public CourseBookings selectCourseBookingsById(Long bookingId);

    /**
     * 查询课程选课记录列表
     *
     * @param courseBookings 课程选课记录
     * @return 课程选课记录集合
     */
    public List<CourseBookings> selectCourseBookingsList(CourseBookings courseBookings);

    /**
     * 查询课程选课记录列表（包含详情）
     *
     * @param courseBookingDTO 课程选课记录DTO
     * @return 课程选课记录集合（包含详情）
     */
    public List<CourseBookingDTO> selectCourseBookingsListWithDetails(CourseBookingDTO courseBookingDTO);

    /**
     * 查询课程选课记录列表（已提交状态）
     *
     * @param courseBookingDTO 课程选课记录DTO
     * @return 课程选课记录集合（已提交状态）
     */
    public List<CourseBookingDTO> selectCourseBookingsListSubmitted(CourseBookingDTO courseBookingDTO);

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
     * 专门的审核更新方法，只更新审核相关字段
     *
     * @param courseBookings 课程选课记录
     * @return 结果
     */
    public int updateCourseBookingsAudit(CourseBookings courseBookings);

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
     * 根据课程ID和学生ID删除选课记录
     *
     * @param courseId 课程ID
     * @param studentId 学生ID
     * @return 结果
     */
    public int deleteCourseBookingsByCourseAndStudent(@Param("courseId") Long courseId, @Param("studentId") String studentId);

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
    public boolean checkIfBooked(@Param("courseId") Long courseId, @Param("studentId") String studentId);

    /**
     * 检查课程选课记录是否存在
     *
     * @param courseId 课程ID
     * @param studentId 学生ID
     * @return 是否已选课
     */
    public boolean checkIfCourseBooked(@Param("courseId") Long courseId, @Param("studentId") String studentId);

    /**
     * 根据课程ID和学生ID删除选课记录（别名方法）
     *
     * @param courseId 课程ID
     * @param studentId 学生ID
     * @return 结果
     */
    public int deleteByCourseAndStudent(@Param("courseId") Long courseId, @Param("studentId") String studentId);

    /**
     * 统计审核状态
     *
     * @param organizer 组织者
     * @return 审核状态统计
     */
    public Map<String, Object> countCourseBookingAuditStatus(String organizer);

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
     * @param courseBookingExportDTO 导出条件
     * @return 导出数据列表
     */
    public List<CourseBookingExportDTO> selectCourseBookingExportList(CourseBookingExportDTO courseBookingExportDTO);

    /**
     * 检查课程选课记录是否存在
     *
     * @param courseId 课程ID
     * @param studentId 学生ID
     * @return 记录数量
     */
    public int checkCourseBookingExists(@Param("courseId") Long courseId, @Param("studentId") String studentId);

    /**
     * 检查课程选课记录是否存在（简单版本）
     *
     * @param courseId 课程ID
     * @param studentId 学生ID
     * @return 包含是否已选课和记录ID的Map
     */
    public Map<String, Object> checkCourseBookingSimple(@Param("courseId") Long courseId, @Param("studentId") String studentId);

    /**
     * 统计总选课人数
     *
     * @return 总选课人数
     */
    public int countTotalCourseBookings();

    /**
     * 根据课程ID统计选课人数
     *
     * @param courseId 课程ID
     * @return 选课人数
     */
    public int countBookingsByCourseId(@Param("courseId") Long courseId);

    /**
     * 原子性选课操作 - 解决并发问题
     *
     * @param courseId 课程ID
     * @param studentId 学生ID
     * @param version 版本号
     * @return 操作结果
     */
    public int signUpWithTransaction(@Param("courseId") Long courseId, @Param("studentId") String studentId, @Param("version") Integer version);

    /**
     * 原子性更新课程容量
     *
     * @param courseId 课程ID
     * @param version 版本号
     * @return 操作结果
     */
    public int updateCapacityAfterSignUp(@Param("courseId") Long courseId, @Param("version") Integer version);

    /**
     * 原子性取消选课操作
     *
     * @param courseId 课程ID
     * @param studentId 学生ID
     * @param version 版本号
     * @return 操作结果
     */
    public int cancelSignUpWithTransaction(@Param("courseId") Long courseId, @Param("studentId") String studentId, @Param("version") Integer version);

    /**
     * 原子性更新课程容量（取消选课后）
     *
     * @param courseId 课程ID
     * @param version 版本号
     * @return 操作结果
     */
    public int updateCapacityAfterCancelSignUp(@Param("courseId") Long courseId, @Param("version") Integer version);

}
