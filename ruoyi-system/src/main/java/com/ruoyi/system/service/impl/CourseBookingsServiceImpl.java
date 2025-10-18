package com.ruoyi.system.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Date;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.CourseBookingsMapper;
import com.ruoyi.system.domain.CourseBookings;
import com.ruoyi.system.domain.CourseBookingDTO;
import com.ruoyi.system.domain.AuditHistory;
import com.ruoyi.system.service.ICourseBookingsService;
import com.ruoyi.system.service.IAuditHistoryService;
import com.ruoyi.system.domain.CourseScore;
import com.ruoyi.system.service.ICourseScoreService;
import org.springframework.transaction.annotation.Transactional;

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

    @Autowired
    private ICourseScoreService courseScoreService;

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
    @Transactional(readOnly = true)
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
                
                // 如果审核通过且有成绩值，创建成绩记录
                if ("已通过".equals(courseBookings.getStatus()) && courseBookings.getScoreValue() != null && !courseBookings.getScoreValue().trim().isEmpty()) {
                    try {
                        // 从数据库获取完整的选课记录信息
                        CourseBookings fullBooking = courseBookingsMapper.selectCourseBookingsByBookingId(courseBookings.getBookingId());
                        if (fullBooking == null) {
                            System.err.println("无法找到选课记录，bookingId: " + courseBookings.getBookingId());
                            return result;
                        }
                        
                        // 检查是否已存在该学生该课程的成绩记录
                        CourseScore existingScore = new CourseScore();
                        existingScore.setStudentId(fullBooking.getStudentId());
                        existingScore.setCourseId(fullBooking.getCourseId().toString());
                        List<CourseScore> existingScores = courseScoreService.selectCourseScoreList(existingScore);
                        
                        CourseScore courseScore;
                        if (existingScores != null && !existingScores.isEmpty()) {
                            // 如果已存在，更新现有记录
                            courseScore = existingScores.get(0);
                            courseScore.setScoreValue(courseBookings.getScoreValue());
                            courseScore.setUploadTime(new Date());
                            
                            // 重新计算GPA
                            BigDecimal gpa = calculateGPA(courseBookings.getScoreValue());
                            if (gpa != null) {
                                courseScore.setGpa(gpa);
                            }
                            
                            int updateResult = courseScoreService.updateCourseScore(courseScore);
                            System.out.println("成绩记录更新结果: " + updateResult);
                        } else {
                            // 如果不存在，创建新记录
                            courseScore = new CourseScore();
                            courseScore.setStudentId(fullBooking.getStudentId());
                            courseScore.setCourseId(fullBooking.getCourseId().toString());
                            courseScore.setScoreValue(courseBookings.getScoreValue());
                            courseScore.setScoreType("正考"); // 默认为正考
                            courseScore.setUploadTime(new Date());
                            
                            // 动态设置学年和学期
                            String[] academicInfo = getCurrentAcademicYearAndSemester();
                            courseScore.setAcademicYear(academicInfo[0]);
                            courseScore.setSemester(academicInfo[1]);
                            
                            // 计算GPA（如果成绩是数字格式）
                            BigDecimal gpa = calculateGPA(courseBookings.getScoreValue());
                            if (gpa != null) {
                                courseScore.setGpa(gpa);
                            }
                            
                            int insertResult = courseScoreService.insertCourseScore(courseScore);
                            System.out.println("成绩记录创建结果: " + insertResult);
                        }
                    } catch (Exception e) {
                        System.err.println("创建/更新成绩记录失败: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
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
     * @param organizer 组织者
     * @return 审核状态统计
     */
    @Override
    public Map<String, Object> countCourseBookingAuditStatus(String organizer)
    {
        return courseBookingsMapper.countCourseBookingAuditStatus(organizer);
    }

    /**
     * 获取当前学年和学期
     *
     * @return 返回数组[学年, 学期]
     */
    private String[] getCurrentAcademicYearAndSemester() {
        LocalDate now = LocalDate.now();
        int year = now.getYear();
        int month = now.getMonthValue();
        
        String academicYear;
        String semester;
        
        // 根据月份判断学期
        if (month >= 9 && month <= 12) {
            // 秋季学期
            academicYear = year + "-" + (year + 1);
            semester = "1";
        } else if (month >= 1 && month <= 6) {
            // 春季学期
            academicYear = (year - 1) + "-" + year;
            semester = "2";
        } else {
            // 夏季学期
            academicYear = (year - 1) + "-" + year;
            semester = "3";
        }
        
        return new String[]{academicYear, semester};
    }

    /**
     * 根据成绩值计算GPA
     *
     * @param scoreValue 成绩值
     * @return GPA值，如果无法计算则返回null
     */
    private BigDecimal calculateGPA(String scoreValue) {
        if (scoreValue == null || scoreValue.trim().isEmpty()) {
            return null;
        }
        
        try {
            // 如果是数字成绩（0-100分制）
            if (scoreValue.matches("^\\d+(\\.\\d+)?$")) {
                double score = Double.parseDouble(scoreValue);
                if (score >= 0 && score <= 100) {
                    // 转换为4.0制GPA
                    if (score >= 90) return new BigDecimal("4.0");
                    else if (score >= 80) return new BigDecimal("3.0");
                    else if (score >= 70) return new BigDecimal("2.0");
                    else if (score >= 60) return new BigDecimal("1.0");
                    else return new BigDecimal("0.0");
                }
            }
            // 如果是等级制成绩
            else if (scoreValue.matches("^[A-F][+-]?$")) {
                switch (scoreValue.toUpperCase()) {
                    case "A+": return new BigDecimal("4.0");
                    case "A": return new BigDecimal("4.0");
                    case "A-": return new BigDecimal("3.7");
                    case "B+": return new BigDecimal("3.3");
                    case "B": return new BigDecimal("3.0");
                    case "B-": return new BigDecimal("2.7");
                    case "C+": return new BigDecimal("2.3");
                    case "C": return new BigDecimal("2.0");
                    case "C-": return new BigDecimal("1.7");
                    case "D+": return new BigDecimal("1.3");
                    case "D": return new BigDecimal("1.0");
                    case "F": return new BigDecimal("0.0");
                    default: return null;
                }
            }
        } catch (NumberFormatException e) {
            // 无法解析为数字，返回null
        }
        
        return null;
    }
}
