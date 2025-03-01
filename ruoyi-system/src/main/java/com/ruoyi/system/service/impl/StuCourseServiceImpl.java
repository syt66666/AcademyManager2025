package com.ruoyi.system.service.impl;

import java.util.List;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.bean.BeanValidators;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.StuCourseMapper;
import com.ruoyi.system.domain.StuCourse;
import com.ruoyi.system.service.IStuCourseService;
import javax.validation.Validator;
/**
 * 课程信息Service业务层处理
 *
 * @author ruoyi
 * @date 2025-03-01
 */
@Service
public class StuCourseServiceImpl implements IStuCourseService
{
    @Autowired
    private StuCourseMapper stuCourseMapper;
    @Autowired
    private Validator validator;

    /**
     * 查询课程信息
     *
     * @param courseId 课程信息主键
     * @return 课程信息
     */
    @Override
    public StuCourse selectStuCourseByCourseId(Long courseId)
    {
        return stuCourseMapper.selectStuCourseByCourseId(courseId);
    }

    /**
     * 查询课程信息列表
     *
     * @param stuCourse 课程信息
     * @return 课程信息
     */
    @Override
    public List<StuCourse> selectStuCourseList(StuCourse stuCourse)
    {
        return stuCourseMapper.selectStuCourseList(stuCourse);
    }

    /**
     * 新增课程信息
     *
     * @param stuCourse 课程信息
     * @return 结果
     */
    @Override
    public int insertStuCourse(StuCourse stuCourse)
    {
        return stuCourseMapper.insertStuCourse(stuCourse);
    }

    /**
     * 修改课程信息
     *
     * @param stuCourse 课程信息
     * @return 结果
     */
    @Override
    public int updateStuCourse(StuCourse stuCourse)
    {
        return stuCourseMapper.updateStuCourse(stuCourse);
    }

    /**
     * 批量删除课程信息
     *
     * @param courseIds 需要删除的课程信息主键
     * @return 结果
     */
    @Override
    public int deleteStuCourseByCourseIds(Long[] courseIds)
    {
        return stuCourseMapper.deleteStuCourseByCourseIds(courseIds);
    }

    /**
     * 删除课程信息信息
     *
     * @param courseId 课程信息主键
     * @return 结果
     */
    @Override
    public int deleteStuCourseByCourseId(Long courseId)
    {
        return stuCourseMapper.deleteStuCourseByCourseId(courseId);
    }

    /**
     * 导入用户数据
     *
     * @param userList 用户数据列表
     * @param isUpdateSupport 是否更新支持，如果已存在，则进行更新数据
     * @param operName 操作用户
     * @return 结果
     */
    @Override
    public String importCourse(List<StuCourse> userList, Boolean isUpdateSupport, String operName)
    {
        if (StringUtils.isNull(userList) || userList.size() == 0)
        {
            throw new ServiceException("导入用户数据不能为空！");
        }
        int successNum = 0;
        int failureNum = 0;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder failureMsg = new StringBuilder();
        for (StuCourse user : userList)
        {
            try
            {
                BeanValidators.validateWithException(validator, user);
                user.setCreateBy(operName);
                this.insertStuCourse(user);
                successNum++;
                successMsg.append("<br/>" + successNum + "、课程代码 " + user.getCourseCode() + " 导入成功");

            }
            catch (Exception e)
            {
                failureNum++;
                String msg = "<br/>" + failureNum + "、课程代码 " + user.getCourseCode() + " 导入失败：";
                failureMsg.append(msg + e.getMessage());
            }
        }
        if (failureNum > 0)
        {
            failureMsg.insert(0, "很抱歉，导入失败！共 " + failureNum + " 条数据格式不正确，错误如下：");
            throw new ServiceException(failureMsg.toString());
        }
        else
        {
            successMsg.insert(0, "恭喜您，数据已全部导入成功！共 " + successNum + " 条，数据如下：");
        }
        return successMsg.toString();
    }



}
