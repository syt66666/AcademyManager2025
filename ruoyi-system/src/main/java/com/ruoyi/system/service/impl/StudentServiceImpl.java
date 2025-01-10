package com.ruoyi.system.service.impl;

import java.util.*;

import com.ruoyi.common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.StudentMapper;
import com.ruoyi.system.domain.Student;
import com.ruoyi.system.service.IStudentService;

/**
 * 学生管理Service业务层处理
 *
 * @author ruoyi
 * @date 2025-01-04
 */
@Service
public class StudentServiceImpl implements IStudentService
{
    @Autowired
    private StudentMapper studentMapper;

    /**
     * 查询学生管理
     *
     * @param id 学生管理主键
     * @return 学生管理
     */
    @Override
    public Student selectStudentById(Long id)
    {
        return studentMapper.selectStudentById(id);
    }

    /**
     * 查询学生管理列表
     *
     * @param student 学生管理
     * @return 学生管理
     */
    @Override
    public List<Student> selectStudentList(Student student)
    {
        return studentMapper.selectStudentList(student);
    }

    /**
     * 新增学生管理
     *
     * @param student 学生管理
     * @return 结果
     */
    @Override
    public int insertStudent(Student student)
    {
        return studentMapper.insertStudent(student);
    }

    /**
     * 修改学生管理
     *
     * @param student 学生管理
     * @return 结果
     */
    @Override
    public int updateStudent(Student student)
    {
        return studentMapper.updateStudent(student);
    }

    /**
     * 批量删除学生管理
     *
     * @param ids 需要删除的学生管理主键
     * @return 结果
     */
    @Override
    public int deleteStudentByIds(Long[] ids)
    {
        return studentMapper.deleteStudentByIds(ids);
    }

    /**
     * 删除学生管理信息
     *
     * @param id 学生管理主键
     * @return 结果
     */
    @Override
    public int deleteStudentById(Long id)
    {
        return studentMapper.deleteStudentById(id);
    }

    /**
     * 查询不同学院转专业人数
     */
    @Override
    public Map<String, ?> echarts1() {
        Map<String, Object> result = new HashMap<>();
        Map<String, Map<String, Integer>> beforeCnt = new HashMap<>();
        Map<String, Map<String, Integer>> afterCnt = new HashMap<>();
        Map<String, Map<String, Map<String, Integer>>> afterMajorChangeType = new HashMap<>();
        List<Student> students = studentMapper.selectStudentList(null);
        for (Student student : students) {
            String beforeAcademy = student.getAcademy();
            String beforeMajor = student.getMajor();
            String afterAcademy = student.getAfterAcademy();
            String afterMajor = student.getAfterMajor();
            String changeType = student.getChange_major_type();

            // 处理转专业前的数据
            if (StringUtils.isNotEmpty(beforeAcademy) && StringUtils.isNotEmpty(beforeMajor)) {
                Map<String, Integer> beforeMap = beforeCnt.computeIfAbsent(beforeAcademy, k -> new HashMap<>());
                beforeMap.put(beforeMajor, beforeMap.getOrDefault(beforeMajor, 0) + 1);
            }

            // 处理转专业后的数据
            if (StringUtils.isNotEmpty(afterAcademy) && StringUtils.isNotEmpty(afterMajor)) {
                Map<String, Integer> afterMap = afterCnt.computeIfAbsent(afterAcademy, k -> new HashMap<>());
                afterMap.put(afterMajor, afterMap.getOrDefault(afterMajor, 0) + 1);

                Map<String, Map<String, Integer>> academyChangeType = afterMajorChangeType.computeIfAbsent(afterAcademy, k -> new HashMap<>());
                Map<String, Integer> majorChangeType = academyChangeType.computeIfAbsent(afterMajor, k -> new HashMap<>());
                majorChangeType.put(changeType, majorChangeType.getOrDefault(changeType, 0) + 1);
            }
        }
        result.put("beforeCnt", beforeCnt);
        result.put("afterCnt", afterCnt);
        result.put("afterMajorChangeType", afterMajorChangeType);
        return result;
    }



}
