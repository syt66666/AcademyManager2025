package com.ruoyi.system.domain;

import java.time.LocalDateTime;

/**
 * 学生账号信息实体类
 */
public class StudentUserInformation {
    /**
     * 学号
     */
    private String studentId;
    /**
     * 学生姓名
     */
    private String studentName;
    /**
     * 学生性别
     */
    private String studentSex;
    /**
     * 登录密码
     */
    private String password = "$2a$10$FmPhZMQDdezHoNOsA3K8rug7KKaE1BrPkHSdnhxmr14MmIinW6y5.";
    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 角色备注
     */
    private String remark = "学生";

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getStudentSex() {
        return studentSex;
    }

    public void setStudentSex(String studentSex) {
        this.studentSex = studentSex;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
