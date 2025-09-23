package com.ruoyi.system.service.impl;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.system.mapper.UserLimitMapper;
import com.ruoyi.system.domain.UserLimit;
import com.ruoyi.system.service.IUserLimitService;
/**
 * 用户取消限制Service业务层处理
 */
@Service
public class UserLimitServiceImpl implements IUserLimitService {
    @Autowired
    private UserLimitMapper userLimitMapper;
    // 每月最大取消次数 - 修改为3次
    private static final int MAX_CANCEL_COUNT_PER_MONTH = 3;
    @Override
    public UserLimit selectUserLimitById(Long id) {
        return userLimitMapper.selectUserLimitById(id);
    }
    @Override
    public List<UserLimit> selectUserLimitList(UserLimit userLimit) {
        return userLimitMapper.selectUserLimitList(userLimit);
    }
    @Override
    public int insertUserLimit(UserLimit userLimit) {
        return userLimitMapper.insertUserLimit(userLimit);
    }
    @Override
    public int updateUserLimit(UserLimit userLimit) {
        return userLimitMapper.updateUserLimit(userLimit);
    }
    @Override
    public int deleteUserLimitByIds(Long[] ids) {
        return userLimitMapper.deleteUserLimitByIds(ids);
    }
    @Override
    public int deleteUserLimitById(Long id) {
        return userLimitMapper.deleteUserLimitById(id);
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean recordCancel(String studentId, Long activityId) {
        try {
            System.out.println("记录取消信息: studentId=" + studentId + ", activityId=" + activityId);
            // 检查表是否存在
            System.out.println("检查user_limite表...");
            // 这里可以添加检查表是否存在的代码
            // 创建新的取消记录
            UserLimit userLimit = new UserLimit();
            userLimit.setStudentId(studentId);
            userLimit.setActivityId(activityId);
            userLimit.setCreatedAt(new Date());
            userLimit.setIsCancel(1);
            System.out.println("插入取消记录: " + userLimit);
            // 插入数据库
            int result = userLimitMapper.insertUserLimit(userLimit);
            System.out.println("插入结果: " + result);
            if (result > 0) {
                // 查询验证
                int newCancelCount = userLimitMapper.selectCancelCountThisMonth(studentId);
                System.out.println("插入后取消次数: " + newCancelCount);
                return true;
            } else {
                System.err.println("插入取消记录失败，影响行数为0");
                return false;
            }
        } catch (Exception e) {
            System.err.println("记录取消信息异常: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("记录取消信息失败", e);
        }
    }
    @Override
    public boolean checkCanCancel(String studentId) {
        int cancelCount = userLimitMapper.selectCancelCountThisMonth(studentId);
        System.out.println("检查取消限制 - 学生ID: " + studentId + ", 取消次数: " + cancelCount);
        return cancelCount < MAX_CANCEL_COUNT_PER_MONTH;
    }
    @Override
    public int getCancelCountThisMonth(String studentId) {
        int count = userLimitMapper.selectCancelCountThisMonth(studentId);
        System.out.println("查询取消次数 - 学生ID: " + studentId + ", 结果: " + count);
        return count;
    }
    // 添加调试方法
    @Override
    public List<UserLimit> selectCancelRecordsByStudentId(String studentId) {
        return userLimitMapper.selectCancelRecordsByStudentId(studentId);
    }
    @Override
    public List<UserLimit> selectAllRecords() {
        return userLimitMapper.selectAllRecords();
    }
    // 获取最大取消次数
    @Override
    public int getMaxCancelCountPerMonth() {
        return MAX_CANCEL_COUNT_PER_MONTH;
    }
}