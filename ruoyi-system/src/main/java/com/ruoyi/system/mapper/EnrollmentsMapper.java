package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.Enrollments;

/**
 * 学生选课记录Mapper接口
 *
 * @author ruoyi
 * @date 2025-04-21
 */
public interface EnrollmentsMapper
{
    /**
     * 查询学生选课记录
     *
     * @param enrollmentId 学生选课记录主键
     * @return 学生选课记录
     */
    public Enrollments selectEnrollmentsByEnrollmentId(Long enrollmentId);

    /**
     * 查询学生选课记录列表
     *
     * @param enrollments 学生选课记录
     * @return 学生选课记录集合
     */
    public List<Enrollments> selectEnrollmentsList(Enrollments enrollments);

    /**
     * 新增学生选课记录
     *
     * @param enrollments 学生选课记录
     * @return 结果
     */
    public int insertEnrollments(Enrollments enrollments);

    /**
     * 修改学生选课记录
     *
     * @param enrollments 学生选课记录
     * @return 结果
     */
    public int updateEnrollments(Enrollments enrollments);

    /**
     * 删除学生选课记录
     *
     * @param enrollmentId 学生选课记录主键
     * @return 结果
     */
    public int deleteEnrollmentsByEnrollmentId(Long enrollmentId);

    /**
     * 批量删除学生选课记录
     *
     * @param enrollmentIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteEnrollmentsByEnrollmentIds(Long[] enrollmentIds);
}
