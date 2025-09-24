package com.ruoyi.system.service.impl;

import java.util.List;
import java.util.Set;
import java.util.HashSet;
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
import com.ruoyi.system.utils.StuInfoDataValidator;

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
    private StuInfoDataValidator dataValidator;

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
        
        // 导入统计信息
        int successNum = 0;
        int failureNum = 0;
        int skipNum = 0;
        int totalNum = stuInfoList.size();
        StringBuilder successMsg = new StringBuilder();
        StringBuilder failureMsg = new StringBuilder();
        StringBuilder skipMsg = new StringBuilder();
        
        // 用于检查重复学号
        Set<String> processedStudentIds = new HashSet<>();
        
        System.out.println("开始处理导入数据，共" + stuInfoList.size() + "条记录");
        
        for (int i = 0; i < stuInfoList.size(); i++) {
            StuInfo stuInfo = stuInfoList.get(i);
            int rowIndex = i + 2; // Excel行号从2开始（第1行是表头）
            
            System.out.println("处理第" + rowIndex + "行数据...");
            
            try {
                // 首先检查stuInfo对象是否为null
                if (stuInfo == null) {
                    System.out.println("❌ 第" + rowIndex + "行数据对象为null");
                    failureNum++;
                    failureMsg.append("<br/>第" + rowIndex + "行：数据解析失败，请检查Excel文件格式");
                    continue;
                }
                
                // 检查学号是否为空
                if (StringUtils.isEmpty(stuInfo.getStudentId())) {
                    failureNum++;
                    failureMsg.append("<br/>第" + rowIndex + "行：学生学号不能为空");
                    continue;
                }
                
                // 检查Excel中是否有重复学号
                if (processedStudentIds.contains(stuInfo.getStudentId())) {
                    failureNum++;
                    failureMsg.append("<br/>第" + rowIndex + "行：学号 " + stuInfo.getStudentId() + " 在Excel中重复");
                    continue;
                }
                processedStudentIds.add(stuInfo.getStudentId());
                
                // 使用数据验证器进行完整验证
                System.out.println("开始验证第" + rowIndex + "行数据...");
                StuInfoDataValidator.ValidationResult validationResult = dataValidator.validate(stuInfo, rowIndex);
                if (!validationResult.isValid()) {
                    System.out.println("❌ 第" + rowIndex + "行数据验证失败: " + validationResult.getErrorMessage());
                    failureNum++;
                    failureMsg.append("<br/>第" + rowIndex + "行：" + validationResult.getErrorMessage());
                    continue;
                }
                System.out.println("✅ 第" + rowIndex + "行数据验证通过");
                
                // 创新班字段已经是Integer类型，不需要转换
                
                // 验证是否存在这个学生
                StuInfo existingStudent = stuInfoMapper.selectStuInfoById(stuInfo.getStudentId());
                if (StringUtils.isNull(existingStudent)) {
                    // 学生不存在，执行新增
                    stuInfo.setCreateBy(operName);
                    int insertResult = this.insertStuInfo(stuInfo);
                    if (insertResult > 0) {
                        successNum++;
                        successMsg.append("<br/>第" + rowIndex + "行：学号 " + stuInfo.getStudentId() + " 新增成功");
                    } else {
                        failureNum++;
                        failureMsg.append("<br/>第" + rowIndex + "行：学号 " + stuInfo.getStudentId() + " 新增失败，数据库操作异常");
                    }
                } else if (isUpdateSupport) {
                    // 学生存在且允许更新
                    stuInfo.setId(existingStudent.getId());
                    stuInfo.setUpdateBy(operName);
                    int updateResult = this.updateStuInfo(stuInfo);
                    if (updateResult > 0) {
                        successNum++;
                        successMsg.append("<br/>第" + rowIndex + "行：学号 " + stuInfo.getStudentId() + " 更新成功");
                    } else {
                        failureNum++;
                        failureMsg.append("<br/>第" + rowIndex + "行：学号 " + stuInfo.getStudentId() + " 更新失败，数据库操作异常");
                    }
                } else {
                    // 学生存在但不允许更新
                    skipNum++;
                    skipMsg.append("<br/>第" + rowIndex + "行：学号 " + stuInfo.getStudentId() + " 已存在，跳过导入");
                }
            } catch (Exception e) {
                failureNum++;
                String errorDetail = e.getMessage();
                if (errorDetail.contains("Duplicate entry")) {
                    errorDetail = "学号已存在，请检查数据或选择更新模式";
                } else if (errorDetail.contains("Data too long")) {
                    errorDetail = "数据长度超出限制";
                } else if (errorDetail.contains("Cannot be null")) {
                    errorDetail = "必填字段为空";
                }
                failureMsg.append("<br/>第" + rowIndex + "行：学号 " + stuInfo.getStudentId() + " 导入失败 - " + errorDetail);
            }
        }
        
        // 构建详细的导入结果报告
        StringBuilder resultMsg = new StringBuilder();
        resultMsg.append("<div style='font-family: Arial, sans-serif;'>");
        resultMsg.append("<h3 style='color: #333; margin-bottom: 15px;'>📊 导入结果统计</h3>");
        resultMsg.append("<div style='background: #f5f5f5; padding: 10px; border-radius: 5px; margin-bottom: 15px;'>");
        resultMsg.append("<p><strong>总数据量：</strong>" + totalNum + " 条</p>");
        resultMsg.append("<p style='color: #67C23A;'><strong>成功导入：</strong>" + successNum + " 条</p>");
        if (skipNum > 0) {
            resultMsg.append("<p style='color: #E6A23C;'><strong>跳过数据：</strong>" + skipNum + " 条</p>");
        }
        resultMsg.append("<p style='color: #F56C6C;'><strong>失败数据：</strong>" + failureNum + " 条</p>");
        resultMsg.append("</div>");
        
        if (successNum > 0) {
            resultMsg.append("<div style='background: #f0f9ff; padding: 10px; border-left: 4px solid #67C23A; margin-bottom: 15px;'>");
            resultMsg.append("<h4 style='color: #67C23A; margin-top: 0;'>✅ 成功导入的数据：</h4>");
            resultMsg.append(successMsg.toString());
            resultMsg.append("</div>");
        }
        
        if (skipNum > 0) {
            resultMsg.append("<div style='background: #fdf6ec; padding: 10px; border-left: 4px solid #E6A23C; margin-bottom: 15px;'>");
            resultMsg.append("<h4 style='color: #E6A23C; margin-top: 0;'>⚠️ 跳过的数据：</h4>");
            resultMsg.append(skipMsg.toString());
            resultMsg.append("</div>");
        }
        
        if (failureNum > 0) {
            resultMsg.append("<div style='background: #fef0f0; padding: 10px; border-left: 4px solid #F56C6C; margin-bottom: 15px;'>");
            resultMsg.append("<h4 style='color: #F56C6C; margin-top: 0;'>❌ 失败的数据：</h4>");
            resultMsg.append(failureMsg.toString());
            resultMsg.append("</div>");
        }
        
        resultMsg.append("</div>");
        
        // 如果有失败的数据，抛出异常
        if (failureNum > 0) {
            throw new ServiceException(resultMsg.toString());
        }
        
        return resultMsg.toString();
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
