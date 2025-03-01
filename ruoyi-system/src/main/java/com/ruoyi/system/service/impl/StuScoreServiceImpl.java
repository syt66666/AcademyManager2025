package com.ruoyi.system.service.impl;

import java.util.List;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.bean.BeanValidators;
import com.ruoyi.system.domain.StuCourse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.StuScoreMapper;
import com.ruoyi.system.domain.StuScore;
import com.ruoyi.system.service.IStuScoreService;

import javax.validation.Validator;

/**
 * 【请填写功能名称】Service业务层处理
 *
 * @author ruoyi
 * @date 2025-03-01
 */
@Service
public class StuScoreServiceImpl implements IStuScoreService
{
    @Autowired
    private StuScoreMapper stuScoreMapper;
    @Autowired
    private Validator validator;
    /**
     * 查询【请填写功能名称】
     *
     * @param scoreId 【请填写功能名称】主键
     * @return 【请填写功能名称】
     */
    @Override
    public StuScore selectStuScoreByScoreId(Long scoreId)
    {
        return stuScoreMapper.selectStuScoreByScoreId(scoreId);
    }

    /**
     * 查询【请填写功能名称】列表
     *
     * @param stuScore 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    @Override
    public List<StuScore> selectStuScoreList(StuScore stuScore)
    {
        return stuScoreMapper.selectStuScoreList(stuScore);
    }

    /**
     * 新增【请填写功能名称】
     *
     * @param stuScore 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int insertStuScore(StuScore stuScore)
    {
        return stuScoreMapper.insertStuScore(stuScore);
    }

    /**
     * 修改【请填写功能名称】
     *
     * @param stuScore 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int updateStuScore(StuScore stuScore)
    {
        return stuScoreMapper.updateStuScore(stuScore);
    }

    /**
     * 批量删除【请填写功能名称】
     *
     * @param scoreIds 需要删除的【请填写功能名称】主键
     * @return 结果
     */
    @Override
    public int deleteStuScoreByScoreIds(Long[] scoreIds)
    {
        return stuScoreMapper.deleteStuScoreByScoreIds(scoreIds);
    }

    /**
     * 删除【请填写功能名称】信息
     *
     * @param scoreId 【请填写功能名称】主键
     * @return 结果
     */
    @Override
    public int deleteStuScoreByScoreId(Long scoreId)
    {
        return stuScoreMapper.deleteStuScoreByScoreId(scoreId);
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
    public String importScore(List<StuScore> userList, boolean updateSupport, String operName)
    {
        if (StringUtils.isNull(userList) || userList.size() == 0)
        {
            throw new ServiceException("导入用户数据不能为空！");
        }
        int successNum = 0;
        int failureNum = 0;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder failureMsg = new StringBuilder();
        for (StuScore user : userList)
        {
            try
            {
                BeanValidators.validateWithException(validator, user);
                user.setCreateBy(operName);
                this.insertStuScore(user);
                successNum++;
                successMsg.append("<br/>" + successNum + "、成绩id " + user.getScoreId() + " 导入成功");

            }
            catch (Exception e)
            {
                failureNum++;
                String msg = "<br/>" + failureNum + "、成绩id " + user.getScoreId() + " 导入失败：";
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
