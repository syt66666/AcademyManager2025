package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.StudentInformation;
import com.ruoyi.system.domain.StudentUserInformation;
import com.ruoyi.system.domain.StudentUserRole;

import java.util.List;

public interface StudentInformationMapper {
    /**
     * 学生表批量插入学生信息
     */
    public void multipleInsertStudent(List<StudentInformation> studentInformationList);

    /**
     * 系统登录用户表插入学生账号信息
     */
    public void multipleInsertStudentAccount(List<StudentUserInformation> studentUserInformationList);

    /**
     * 获取插入学生账号信息后对应主键
     */
    public List<Integer> getInsertStudentAccountIds(List<StudentUserInformation> studentUserInformationList);
    /**
     * 批量插入用户权限关联表
     */
    public void multipleInsertUserRole(List<StudentUserRole> studentUserRoleList);
}
