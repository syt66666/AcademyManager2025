package com.ruoyi.system.mapper;
import java.util.List;
import com.ruoyi.system.domain.UserLimit;
import org.apache.ibatis.annotations.Param;

/**
 * 用户取消限制Mapper接口
 */
public interface UserLimitMapper {
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
     * 删除用户取消限制
     */
    public int deleteUserLimitById(Long id);
    /**
     * 批量删除用户取消限制
     */
    public int deleteUserLimitByIds(Long[] ids);
    /**
     * 检查是否已存在取消记录
     */
    public int checkCancelRecord(@Param("studentId") String studentId, @Param("activityId") Long activityId);
    /**
     * 查询用户本月取消次数
     */
    public int selectCancelCountThisMonth(@Param("studentId") String studentId);
    /**
     * 查询用户所有取消记录
     */
    public List<UserLimit> selectCancelRecordsByStudentId(@Param("studentId") String studentId);
    /**
     * 查询所有记录（调试用）
     */
    public List<UserLimit> selectAllRecords();
}