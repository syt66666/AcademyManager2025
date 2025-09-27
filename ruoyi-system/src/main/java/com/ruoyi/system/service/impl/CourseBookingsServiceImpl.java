package com.ruoyi.system.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.CourseBookingsMapper;
import com.ruoyi.system.mapper.AuditHistoryMapper;
import com.ruoyi.system.domain.CourseBookings;
import com.ruoyi.system.domain.AuditHistory;
import com.ruoyi.system.domain.dto.CourseBookingDTO;
import com.ruoyi.system.domain.dto.CourseBookingExportDTO;
import com.ruoyi.system.service.ICourseBookingsService;
import com.ruoyi.system.service.ISysUserService;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.ServletUtils;
import com.ruoyi.common.utils.ip.IpUtils;
import com.ruoyi.common.exception.ServiceException;

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
    private AuditHistoryMapper auditHistoryMapper;
    
    @Autowired
    private ISysUserService userService;

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
        // 获取审核前的状态
        CourseBookings beforeBooking = courseBookingsMapper.selectCourseBookingsByBookingId(courseBookings.getBookingId());
        String beforeStatus = beforeBooking != null ? beforeBooking.getStatus() : null;
        
        // 执行审核更新
        int result = courseBookingsMapper.updateCourseBookingsAudit(courseBookings);
        
        // 如果更新成功，保存审核历史
        if (result > 0) {
            saveAuditHistory(courseBookings.getBookingId(), beforeStatus, 
                           courseBookings.getStatus(), courseBookings.getReviewerComment());
        }
        
        return result;
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

    /**
     * 查询课程选课记录列表（审核列表）
     *
     * @param courseBookingDTO 查询条件
     * @return 课程选课记录集合
     */
    @Override
    public List<CourseBookingDTO> selectCourseBookingsListAudit(CourseBookingDTO courseBookingDTO)
    {
        return courseBookingsMapper.selectCourseBookingsListAudit(courseBookingDTO);
    }

    /**
     * 获取课程选课记录详细信息（包含证明材料和总结文档）
     *
     * @param bookingId 课程选课记录主键
     * @return 课程选课记录
     */
    @Override
    public CourseBookingDTO selectCourseBookingsByBookingIdWithDetails(Long bookingId)
    {
        return courseBookingsMapper.selectCourseBookingsByBookingIdWithDetails(bookingId);
    }

    // 新增方法：保存审核历史
    private void saveAuditHistory(Long bookingId, String beforeStatus,
                                  String afterStatus, String remark) {
        AuditHistory history = new AuditHistory();

        // 填充模块信息
        history.setModuleType(6);  // 6代表课程选课模块
        history.setModuleId(bookingId);

        // 状态信息
        history.setAuditStatusBefore(beforeStatus);
        history.setAuditStatusAfter(afterStatus);

        // 操作信息
        history.setAuditAction(getAuditAction(beforeStatus, afterStatus));
        history.setAuditRemark(remark);

        // 用户信息
        SysUser currentUser = userService.selectUserById(SecurityUtils.getUserId());
        history.setAuditorId(currentUser.getUserName());

        // 系统信息
        history.setAuditTime(new Date());
        history.setIpAddress(IpUtils.getIpAddr());
        history.setDeviceInfo(ServletUtils.getRequest().getHeader("User-Agent"));

        // 插入记录
        if (auditHistoryMapper.insertAuditHistory(history) <= 0) {
            throw new ServiceException("审核记录保存失败");
        }
    }

    // 辅助方法：判断审核动作
    private String getAuditAction(String beforeStatus, String afterStatus) {
        if ("已通过".equals(afterStatus)) {
            return "审核通过";
        } else if ("未通过".equals(afterStatus)) {
            return "审核拒绝";
        } else if ("待审核".equals(afterStatus)) {
            return "撤回审核";
        }
        return "状态变更";
    }
}
