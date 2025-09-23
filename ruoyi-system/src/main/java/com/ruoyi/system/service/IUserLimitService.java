package com.ruoyi.system.service;
import java.util.List;
import com.ruoyi.system.domain.UserLimit;
/**
 * 用户取消限制Service接口
 */
public interface IUserLimitService {
    /**
     * 查询用户取消限制
     */
    public UserLimit selectUserLimitById(Long id);
    /**
     * 查询用户取消限制列表
     */
    public List<UserLimit> selectUserLimitList(UserLimit userLimit);
    /**
     * 新增用户取消限制
     */
    public int insertUserLimit(UserLimit userLimit);
    /**
     * 修改用户取消限制
     */
    public int updateUserLimit(UserLimit userLimit);
    /**
     * 批量删除用户取消限制
     */
    public int deleteUserLimitByIds(Long[] ids);
    /**
     * 删除用户取消限制信息
     */
    public int deleteUserLimitById(Long id);
    /**
     * 记录取消信息
     */
    public boolean recordCancel(String studentId, Long activityId);
    /**
     * 检查是否可以取消
     */
    public boolean checkCanCancel(String studentId);
    /**
     * 获取用户本月取消次数
     */
    public int getCancelCountThisMonth(String studentId);
    /**
     * 查询用户所有取消记录
     */
    public List<UserLimit> selectCancelRecordsByStudentId(String studentId);
    /**
     * 查询所有记录（调试用）
     */
    public List<UserLimit> selectAllRecords();
    /**
     * 获取最大取消次数
     */
    public int getMaxCancelCountPerMonth();
}