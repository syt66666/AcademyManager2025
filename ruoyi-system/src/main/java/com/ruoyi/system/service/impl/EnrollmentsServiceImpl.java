package com.ruoyi.system.service.impl;

import java.util.List;

import com.ruoyi.system.domain.StuCourse;
import com.ruoyi.system.domain.StuEnrollments;
import com.ruoyi.system.mapper.StuCourseMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.EnrollmentsMapper;
import com.ruoyi.system.domain.Enrollments;
import com.ruoyi.system.service.IEnrollmentsService;
import org.springframework.transaction.annotation.Transactional;

/**
 * 学生选课记录Service业务层处理
 *
 * @author ruoyi
 * @date 2025-04-21
 */
@Service
public class EnrollmentsServiceImpl implements IEnrollmentsService {
    @Autowired
    private EnrollmentsMapper enrollmentsMapper;
    @Autowired
    private StuCourseMapper stuCourseMapper;

    /**
     * 查询学生选课记录
     *
     * @param enrollmentId 学生选课记录主键
     * @return 学生选课记录
     */
    @Override
    public Enrollments selectEnrollmentsByEnrollmentId(Long enrollmentId) {
        return enrollmentsMapper.selectEnrollmentsByEnrollmentId(enrollmentId);
    }

    /**
     * 查询学生选课记录列表
     *
     * @param enrollments 学生选课记录
     * @return 学生选课记录
     */
    @Override
    public List<Enrollments> selectEnrollmentsList(Enrollments enrollments) {
        return enrollmentsMapper.selectEnrollmentsList(enrollments);
    }
    @Override
    public List<StuEnrollments> selectStuEnrollmentsList(StuEnrollments enrollments) {
        return enrollmentsMapper.selectStudentEnrollmentsList(enrollments);
    }
    /**
     * 新增学生选课记录
     *
     * @param enrollments 学生选课记录
     * @return 结果
     */
    @Override
    public int insertEnrollments(Enrollments enrollments) {
        // 1. 获取课程信息（避免重复查询）
        StuCourse course = stuCourseMapper.selectStuCourseByCourseCode(enrollments.getCourseCode());
        if (course == null) {
            throw new RuntimeException("课程不存在"); // 可选：增加异常处理
        }

        // 2. 检查课程容量
        Long enrolledStudent = course.getEnrolledStudent(); // 获取当前选课人数
        if (enrolledStudent == null) { // 如果为null，初始化为0
            enrolledStudent = 0L;
        }

        if (enrolledStudent >= course.getCourseCapacity()) {
            return 0; // 返回0表示选课失败
        }

        // 3. 名额未满时更新选课人数
        enrolledStudent += 1; // 增加选课人数
        course.setEnrolledStudent(enrolledStudent); // 设置新的选课人数
        System.out.println("更新后的课程容量：" + course.getEnrolledStudent());

        // 4. 更新数据库中的课程信息
        stuCourseMapper.updateStuCourse(course); // 需要确保有对应的update方法
        return enrollmentsMapper.insertEnrollments(enrollments); // 返回1表示选课成功
    }


    /**
     * 修改学生选课记录
     *
     * @param enrollments 学生选课记录
     * @return 结果
     */
    @Override
    public int updateEnrollments(Enrollments enrollments) {
        return enrollmentsMapper.updateEnrollments(enrollments);
    }

    /**
     * 批量删除学生选课记录
     *
     * @param enrollmentIds 需要删除的学生选课记录主键
     * @return 结果
     */
    @Override
    public int deleteEnrollmentsByEnrollmentIds(Long[] enrollmentIds) {
        return enrollmentsMapper.deleteEnrollmentsByEnrollmentIds(enrollmentIds);
    }

    /**
     * 删除学生选课记录信息
     *
     * @param enrollmentId 学生选课记录主键
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class) // 添加事务控制
    @Override
    public int deleteEnrollmentsByEnrollmentId(Long enrollmentId) {
        Enrollments enrollment = enrollmentsMapper.selectEnrollmentsByEnrollmentId(enrollmentId);
        if (enrollment == null) {
            return 0; // 或抛出自定义异常
        }

        StuCourse course = stuCourseMapper.selectStuCourseByCourseCode(enrollment.getCourseCode());
        if (course != null) {
            course.setEnrolledStudent(Math.max(course.getEnrolledStudent() - 1, 0));
            System.out.println("更新后的课程容量：" + course.getEnrolledStudent());
            stuCourseMapper.updateStuCourse(course);
        }

        return enrollmentsMapper.deleteEnrollmentsByEnrollmentId(enrollmentId);
    }
}
