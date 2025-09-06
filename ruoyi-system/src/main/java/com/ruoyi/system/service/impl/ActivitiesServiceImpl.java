package com.ruoyi.system.service.impl;

import java.util.List;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.bean.BeanValidators;
import com.ruoyi.system.domain.Activities;
import com.ruoyi.system.domain.StuCourse;
import com.ruoyi.system.mapper.ActivitiesMapper;
import com.ruoyi.system.service.IActivitiesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.Validator;

/**
 * 活动信息 服务层实现
 */
@Service
public class ActivitiesServiceImpl implements IActivitiesService {
    @Autowired
    private ActivitiesMapper activitiesMapper;
    @Autowired
    private Validator validator;

    /**
     * 查询活动信息
     *
     * @param activityId 活动ID
     * @return 活动信息
     */
    @Override
    public Activities selectActivityById(Integer activityId) {
        return activitiesMapper.selectActivityById(activityId);
    }

    /**
     * 查询活动列表
     *
     * @param activity 活动信息
     * @return 活动
     */
    @Override
    public List<Activities> selectActivityList(Activities activity) {
        return activitiesMapper.selectActivityList(activity);
    }

    /**
     * 新增活动
     *
     * @param activity 活动信息
     * @return 结果
     */
    @Override
    public int insertActivity(Activities activity) {
        // 检查活动名称和组织单位的唯一性
        if (!checkActivityUnique(activity.getActivityName(), activity.getOrganizer(), null)) {
            throw new ServiceException("活动名称和组织单位组合已存在，不能重复添加！");
        }
        
        // 转换活动类型（支持文字和数字两种格式）
        try {
            String convertedType = convertActivityTypeToNumber(activity.getActivityType());
            activity.setActivityType(convertedType);
        } catch (ServiceException e) {
            throw e; // 重新抛出转换异常
        }
        
        if(activity.getActivityCapacity()==null){
            activity.setActivityCapacity(activity.getActivityTotalCapacity());
        }
        return activitiesMapper.insertActivity(activity);
    }

    /**
     * 修改活动
     *
     * @param activity 活动信息
     * @return 结果
     */
    @Override
    public int updateActivity(Activities activity) {
        /*int result = activitiesMapper.updateActivity(activity);
        if (result == 0) {
            throw new ServiceException("数据出现错误，请刷新重试！");
        }
        return result;*/
        return activitiesMapper.updateActivity(activity);
    }
    @Override
    public int updateActivity2(Activities activity) {
        // 检查活动名称和组织单位的唯一性（编辑时排除自身）
        if (!checkActivityUnique(activity.getActivityName(), activity.getOrganizer(), activity.getActivityId())) {
            throw new ServiceException("活动名称和组织单位组合已存在，不能重复添加！");
        }
        
        // 转换活动类型（支持文字和数字两种格式）
        try {
            String convertedType = convertActivityTypeToNumber(activity.getActivityType());
            activity.setActivityType(convertedType);
        } catch (ServiceException e) {
            throw e; // 重新抛出转换异常
        }
        
        return activitiesMapper.updateActivity2(activity);
    }
    /**
     * 批量删除活动
     *
     * @param activityIds 需要删除的活动ID
     * @return 结果
     */
    @Override
    public int deleteActivityByIds(Integer[] activityIds) {
        return activitiesMapper.deleteActivityByIds(activityIds);
    }

    /**
     * 删除活动信息
     *
     * @param activityId 活动ID
     * @return 结果
     */
    @Override
    public int deleteActivityById(Integer activityId) {
        return activitiesMapper.deleteActivityById(activityId);
    }

    /**
     * 导入活动信息
     *
     * @param userList 活动ID
     * @return 结果
     */
    @Override
    public String importActivity(List<Activities> userList, boolean updateSupport, String operName) {
        if (StringUtils.isNull(userList) || userList.isEmpty()) {
            throw new ServiceException("导入用户数据不能为空！");
        }
        int successNum = 0;
        int failureNum = 0;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder failureMsg = new StringBuilder();
        for (Activities user : userList) {
            try {
                BeanValidators.validateWithException(validator, user);
                user.setCreateBy(operName);
                
                // 转换活动类型（支持文字和数字两种格式）
                String convertedType = convertActivityTypeToNumber(user.getActivityType());
                user.setActivityType(convertedType);
                
                // 直接调用insertActivity，它内部会进行唯一性检查
                this.insertActivity(user);
                successNum++;
                successMsg.append("<br/>" + successNum + "、活动名称 " + user.getActivityName() + " 导入成功");

            } catch (Exception e) {
                failureNum++;
                String msg = "<br/>" + failureNum + "、活动名称 " + user.getActivityName() + " 导入失败：";
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

    /**
     * 修改活动容量
     */
    @Override
    public int increaseCapacity(Integer activityId,Integer version) {
        Activities activity = activitiesMapper.selectActivityById(activityId);
        if (activity == null) {
            throw new ServiceException("活动不存在！");
        }
        int result = activitiesMapper.increaseCapacity(activityId, version);
        if (result == 0) {
            throw new ServiceException("数据出现错误，请刷新重试！");
        }
        return result;
    }

    @Override
    public int decreaseCapacity(Integer activityId,Integer version) {
        Activities activity = activitiesMapper.selectActivityById(activityId);
        if (activity == null) {
            throw new ServiceException("活动不存在！");
        }
        int result = activitiesMapper.decreaseCapacity(activityId, version);
        if (result == 0) {
            throw new ServiceException("数据出现错误，请刷新重试！");
        }
        return result;
    }

    @Override
    public boolean checkActivityUnique(String activityName, String organizer, Integer activityId) {
        int count = activitiesMapper.checkActivityUnique(activityName, organizer, activityId);
        return count == 0;
    }

    /**
     * 将活动类型文字转换为数字
     * @param activityTypeText 活动类型文字
     * @return 对应的数字字符串
     */
    private String convertActivityTypeToNumber(String activityTypeText) {
        if (activityTypeText == null || activityTypeText.trim().isEmpty()) {
            return null;
        }
        
        String trimmed = activityTypeText.trim();
        
        // 映射关系
        if (trimmed.equals("人格塑造与价值引领活动类")) {
            return "1";
        } else if (trimmed.equals("知识融合与思维进阶活动类")) {
            return "2";
        } else if (trimmed.equals("能力锻造与实践创新活动类")) {
            return "3";
        } else if (trimmed.equals("社会责任与领军意识活动类")) {
            return "4";
        } else if (trimmed.equals("1") || trimmed.equals("2") || trimmed.equals("3") || trimmed.equals("4")) {
            // 如果已经是数字，直接返回
            return trimmed;
        } else {
            // 如果都不匹配，抛出异常
            throw new ServiceException("无效的活动类型：" + activityTypeText + "，支持的类型：人格塑造与价值引领活动类、知识融合与思维进阶活动类、能力锻造与实践创新活动类、社会责任与领军意识活动类");
        }
    }
}
