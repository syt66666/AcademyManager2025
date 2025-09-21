package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.StuInfoMapper;
import com.ruoyi.system.domain.StuInfo;
import com.ruoyi.system.service.IStuInfoService;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.exception.ServiceException;

/**
 * 学生信息Service业务层处理
 *
 * @author ruoyi
 * @date 2025-03-27
 */
@Service
public class StuInfoServiceImpl implements IStuInfoService
{
    @Autowired
    private StuInfoMapper stuInfoMapper;

    

    /**
     * 查询学生信息列表
     *
     * @param stuInfo 学生信息
     * @return 学生信息
     */
    @Override
    public List<StuInfo> selectStuInfoList(StuInfo stuInfo)
    {
        return stuInfoMapper.selectStuInfoList(stuInfo);
    }

    /**
     * 新增学生信息
     *
     * @param stuInfo 学生信息
     * @return 结果
     */
    @Override
    public int insertStuInfo(StuInfo stuInfo)
    {
        return stuInfoMapper.insertStuInfo(stuInfo);
    }

    /**
     * 修改学生信息
     *
     * @param stuInfo 学生信息
     * @return 结果
     */
    @Override
    public int updateStuInfo(StuInfo stuInfo)
    {
        return stuInfoMapper.updateStuInfo(stuInfo);
    }

    /**
     * 批量删除学生信息
     *
     * @param ids 需要删除的学生信息主键
     * @return 结果
     */
    @Override
    public int deleteStuInfoByIds(Long[] ids)
    {
        return stuInfoMapper.deleteStuInfoByIds(ids);
    }

    /**
     * 删除学生信息信息
     *
     * @param id 学生信息主键
     * @return 结果
     */
    @Override
    public int deleteStuInfoById(Long id)
    {
        return stuInfoMapper.deleteStuInfoById(id);
    }
    /**
     * 查询学生信息
     *
     * @param studentId 学生信息主键
     * @return 学生信息
     */
    @Override
    public StuInfo getStudentInfoById(String studentId) {
        return stuInfoMapper.selectStuInfoById(studentId);
    }

    @Override
    public List<StuInfo> getStudentChangeMajorInfo(StuInfo stuInfo) {
        return stuInfoMapper.getStudentChangeMajorInfo(stuInfo);
    }

    /**
     * 导入学生信息数据
     *
     * @param stuInfoList 学生信息数据列表
     * @param isUpdateSupport 是否更新支持，如果已存在，则进行更新数据
     * @param operName 操作用户
     * @return 结果
     */
    @Override
    public String importStuInfo(List<StuInfo> stuInfoList, Boolean isUpdateSupport, String operName) {
        if (StringUtils.isNull(stuInfoList) || stuInfoList.size() == 0) {
            throw new ServiceException("导入学生数据不能为空！");
        }
        int successNum = 0;
        int failureNum = 0;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder failureMsg = new StringBuilder();
        for (StuInfo stuInfo : stuInfoList) {
            try {
                // 验证必填字段
                if (StringUtils.isEmpty(stuInfo.getStudentId())) {
                    throw new ServiceException("学生学号不能为空");
                }
                if (StringUtils.isEmpty(stuInfo.getStudentName())) {
                    throw new ServiceException("学生姓名不能为空");
                }
                if (StringUtils.isEmpty(stuInfo.getAcademy())) {
                    throw new ServiceException("所属书院不能为空");
                }
                if (StringUtils.isEmpty(stuInfo.getOriginalSystemMajor())) {
                    throw new ServiceException("原系统内专业不能为空");
                }
                if (StringUtils.isEmpty(stuInfo.getStudentClass())) {
                    throw new ServiceException("行政班不能为空");
                }
                if (StringUtils.isEmpty(stuInfo.getDivertForm())) {
                    throw new ServiceException("分流形式不能为空");
                }
                
                // 验证是否存在这个学生
                StuInfo s = stuInfoMapper.selectStuInfoById(stuInfo.getStudentId());
                if (StringUtils.isNull(s)) {
                    stuInfo.setCreateBy(operName);
                    this.insertStuInfo(stuInfo);
                    successNum++;
                    successMsg.append("<br/>" + successNum + "、学生学号 " + stuInfo.getStudentId() + " 导入成功");
                } else if (isUpdateSupport) {
                    stuInfo.setUpdateBy(operName);
                    this.updateStuInfo(stuInfo);
                    successNum++;
                    successMsg.append("<br/>" + successNum + "、学生学号 " + stuInfo.getStudentId() + " 更新成功");
                } else {
                    failureNum++;
                    failureMsg.append("<br/>" + failureNum + "、学生学号 " + stuInfo.getStudentId() + " 已存在");
                }
            } catch (Exception e) {
                failureNum++;
                String msg = "<br/>" + failureNum + "、学生学号 " + stuInfo.getStudentId() + " 导入失败：";
                failureMsg.append(msg + e.getMessage());
            }
        }
        if (failureNum > 0) {
            failureMsg.insert(0, "很抱歉，导入失败！共 " + failureNum + " 条数据格式不正确，错误如下：");
            throw new ServiceException(failureMsg.toString());
        } else {
            successMsg.insert(0, "恭喜您，数据已全部导入成功！共 " + successNum + " 条，数据如下：");
        }
        return successMsg.toString();
    }
}
