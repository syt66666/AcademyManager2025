package com.ruoyi.system.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.CourseBookingsMapper;
import com.ruoyi.system.domain.CourseBookings;
import com.ruoyi.system.domain.CourseBookingDTO;
import com.ruoyi.system.domain.AuditHistory;
import com.ruoyi.system.service.ICourseBookingsService;
import com.ruoyi.system.service.IAuditHistoryService;

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

    @Autowired
    private IAuditHistoryService auditHistoryService;

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

    /**
     * 检查学生是否选课了指定课程
     *
     * @param courseId 课程ID
     * @param studentId 学生ID
     * @return 是否已选课
     */
    @Override
    public boolean checkIfBooked(Long courseId, String studentId)
    {
        return courseBookingsMapper.checkIfBooked(courseId, studentId);
    }

    /**
     * 根据课程ID和学生ID删除选课记录
     *
     * @param courseId 课程ID
     * @param studentId 学生ID
     * @return 结果
     */
    @Override
    public int deleteByCourseAndStudent(Long courseId, String studentId)
    {
        return courseBookingsMapper.deleteByCourseAndStudent(courseId, studentId);
    }

    /**
     * 查询课程选课记录列表（包含详情）
     *
     * @param courseBookingDTO 课程选课记录DTO
     * @return 课程选课记录集合（包含详情）
     */
    @Override
    public List<CourseBookingDTO> selectCourseBookingsListWithDetails(CourseBookingDTO courseBookingDTO)
    {
        return courseBookingsMapper.selectCourseBookingsListWithDetails(courseBookingDTO);
    }

    /**
     * 查询课程选课记录列表（审核列表）
     *
     * @param courseBookingDTO 课程选课记录DTO
     * @return 课程选课记录集合（审核列表）
     */
    @Override
    public List<CourseBookingDTO> selectCourseBookingsListAudit(CourseBookingDTO courseBookingDTO)
    {
        return courseBookingsMapper.selectCourseBookingsListAudit(courseBookingDTO);
    }

    /**
     * 根据ID查询课程选课记录（包含详情）
     *
     * @param bookingId 课程选课记录主键
     * @return 课程选课记录（包含详情）
     */
    @Override
    public CourseBookingDTO selectCourseBookingsByBookingIdWithDetails(Long bookingId)
    {
        return courseBookingsMapper.selectCourseBookingsByBookingIdWithDetails(bookingId);
    }

    /**
     * 专门的审核更新方法，只更新审核相关字段
     *
     * @param courseBookings 课程选课记录
     * @return 结果
     */
    @Override
    public int updateCourseBookingsAudit(CourseBookings courseBookings)
    {
        // 获取审核前的状态
        CourseBookings beforeAudit = courseBookingsMapper.selectCourseBookingsByBookingId(courseBookings.getBookingId());
        String statusBefore = beforeAudit != null ? beforeAudit.getStatus() : "未知";
        
        // 更新审核记录
        int result = courseBookingsMapper.updateCourseBookingsAudit(courseBookings);
        
        // 如果更新成功，创建审核历史记录
        if (result > 0) {
            try {
                AuditHistory auditHistory = new AuditHistory();
                auditHistory.setModuleType(6); // 课程选课模块类型
                auditHistory.setModuleId(courseBookings.getBookingId());
                auditHistory.setAuditAction("已通过".equals(courseBookings.getStatus()) ? "通过" : "拒绝");
                auditHistory.setAuditStatusBefore(statusBefore);
                auditHistory.setAuditStatusAfter(courseBookings.getStatus());
                auditHistory.setAuditorId(courseBookings.getReviewer());
                auditHistory.setAuditTime(new Date());
                auditHistory.setAuditRemark(courseBookings.getReviewerComment());
                
                // 创建审核历史记录
                int historyResult = auditHistoryService.insertAuditHistory(auditHistory);
                System.out.println("审核历史记录创建结果: " + historyResult);
            } catch (Exception e) {
                System.err.println("创建审核历史记录失败: " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        return result;
    }

    /**
     * 统计审核状态
     *
     * @return 审核状态统计
     */
    @Override
    public Map<String, Object> countCourseBookingAuditStatus()
    {
        return courseBookingsMapper.countCourseBookingAuditStatus();
    }
}
