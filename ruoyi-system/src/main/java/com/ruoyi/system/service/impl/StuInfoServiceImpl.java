package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.system.mapper.StuInfoMapper;
import com.ruoyi.system.domain.StuInfo;
import com.ruoyi.system.service.IStuInfoService;
import com.ruoyi.system.service.ISysUserService;
import com.ruoyi.system.mapper.SysUserMapper;
import com.ruoyi.system.mapper.SysUserRoleMapper;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.constant.UserConstants;
import com.ruoyi.common.core.domain.entity.SysUser;

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

    @Autowired
    private ISysUserService userService;

    @Autowired
    private SysUserMapper userMapper;

    @Autowired
    private SysUserRoleMapper userRoleMapper;

    

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
    @Transactional
    public int insertStuInfo(StuInfo stuInfo)
    {
        // 插入学生信息
        int result = stuInfoMapper.insertStuInfo(stuInfo);
        
        // 同时创建对应的用户账号
        if (result > 0) {
            createStudentUser(stuInfo);
        }
        
        return result;
    }

    /**
     * 为学生创建用户账号
     *
     * @param stuInfo 学生信息
     */
    private void createStudentUser(StuInfo stuInfo) {
        try {
            // 检查用户是否已存在
            SysUser existingUser = userService.selectUserByUserName(stuInfo.getStudentId());
            if (existingUser != null) {
                // 用户已存在，跳过创建
                return;
            }

            // 创建新用户
            SysUser user = new SysUser();
            user.setUserName(stuInfo.getStudentId()); // 用户名使用学号
            user.setNickName(stuInfo.getStudentName()); // 昵称使用姓名
            user.setPassword(SecurityUtils.encryptPassword("123456")); // 默认密码123456
            user.setStatus(UserConstants.NORMAL); // 正常状态
            user.setDelFlag(UserConstants.NORMAL); // 未删除
            user.setSex(stuInfo.getStudentSex()); // 性别
            user.setRemark("学生"); // 备注
            user.setRoleIds(new Long[]{101L}); // 设置学生角色ID
            
            // 插入用户（会自动处理角色分配）
            userService.insertUser(user);
        } catch (Exception e) {
            // 用户创建失败不影响学生信息插入，只记录日志
            System.err.println("创建学生用户失败: " + e.getMessage());
        }
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
    @Transactional
    public int deleteStuInfoByIds(Long[] ids)
    {
        // 先删除对应的用户和角色
        for (Long id : ids) {
            deleteStudentUserAndRole(id);
        }
        // 再删除学生信息
        return stuInfoMapper.deleteStuInfoByIds(ids);
    }

    /**
     * 删除学生信息信息
     *
     * @param id 学生信息主键
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteStuInfoById(Long id)
    {
        // 先删除对应的用户和角色
        deleteStudentUserAndRole(id);
        // 再删除学生信息
        return stuInfoMapper.deleteStuInfoById(id);
    }

    /**
     * 删除学生对应的用户和角色
     *
     * @param stuInfoId 学生信息主键
     */
    private void deleteStudentUserAndRole(Long stuInfoId) {
        try {
            // 根据学生信息主键ID获取学生信息
            StuInfo stuInfo = stuInfoMapper.selectStuInfoByPrimaryId(stuInfoId);
            System.out.println("删除学生用户 - 学生信息: " + (stuInfo != null ? stuInfo.getStudentId() : "null"));
            
            if (stuInfo != null && StringUtils.isNotEmpty(stuInfo.getStudentId())) {
                // 根据学号查找对应的用户
                SysUser user = userService.selectUserByUserName(stuInfo.getStudentId());
                System.out.println("删除学生用户 - 找到用户: " + (user != null ? user.getUserId() : "null"));
                
                if (user != null) {
                    // 先删除用户角色关联
                    int roleResult = userRoleMapper.deleteUserRoleByUserId(user.getUserId());
                    System.out.println("删除学生用户 - 删除角色关联结果: " + roleResult);
                    
                    // 物理删除用户（直接执行DELETE SQL）
                    int userResult = userMapper.physicalDeleteUserById(user.getUserId());
                    System.out.println("删除学生用户 - 删除用户结果: " + userResult);
                } else {
                    System.out.println("删除学生用户 - 未找到对应用户，学号: " + stuInfo.getStudentId());
                }
            } else {
                System.out.println("删除学生用户 - 学生信息为空或学号为空");
            }
        } catch (Exception e) {
            // 用户删除失败不影响学生信息删除，只记录日志
            System.err.println("删除学生用户失败: " + e.getMessage());
            e.printStackTrace();
        }
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
                
                // 处理创新班字段转换：是/否 -> 1/0
                if (stuInfo.getInnovationClass() != null) {
                    // 如果已经是数字类型，保持不变
                    if (stuInfo.getInnovationClass() instanceof Integer) {
                        // 已经是数字，不需要转换
                    } else {
                        // 如果是字符串类型，进行转换
                        String innovationClassStr = stuInfo.getInnovationClass().toString();
                        if ("是".equals(innovationClassStr) || "1".equals(innovationClassStr)) {
                            stuInfo.setInnovationClass(1);
                        } else if ("否".equals(innovationClassStr) || "0".equals(innovationClassStr)) {
                            stuInfo.setInnovationClass(0);
                        } else if (StringUtils.isEmpty(innovationClassStr)) {
                            stuInfo.setInnovationClass(null);
                        } else {
                            throw new ServiceException("创新班字段值无效，请输入'是'、'否'、'1'或'0'");
                        }
                    }
                }
                
                // 验证是否存在这个学生
                StuInfo s = stuInfoMapper.selectStuInfoById(stuInfo.getStudentId());
                if (StringUtils.isNull(s)) {
                    stuInfo.setCreateBy(operName);
                    // 使用insertStuInfo方法，会自动创建用户和角色
                    int insertResult = this.insertStuInfo(stuInfo);
                    if (insertResult > 0) {
                        successNum++;
                        successMsg.append("<br/>" + successNum + "、学生学号 " + stuInfo.getStudentId() + " 导入成功");
                    } else {
                        failureNum++;
                        failureMsg.append("<br/>" + failureNum + "、学生学号 " + stuInfo.getStudentId() + " 导入失败");
                    }
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

    /**
     * 重置学生密码
     *
     * @param id 学生信息主键
     * @return 结果
     */
    @Override
    public int resetStudentPassword(Long id) {
        try {
            // 根据学生信息主键ID获取学生信息
            StuInfo stuInfo = stuInfoMapper.selectStuInfoByPrimaryId(id);
            if (stuInfo != null && StringUtils.isNotEmpty(stuInfo.getStudentId())) {
                // 根据学号查找对应的用户
                SysUser user = userService.selectUserByUserName(stuInfo.getStudentId());
                if (user != null) {
                    // 重置密码为123456
                    user.setPassword(SecurityUtils.encryptPassword("123456"));
                    return userMapper.updateUser(user);
                } else {
                    throw new ServiceException("未找到对应的用户账号");
                }
            } else {
                throw new ServiceException("未找到学生信息");
            }
        } catch (Exception e) {
            throw new ServiceException("重置密码失败: " + e.getMessage());
        }
    }
}
