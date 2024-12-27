package com.ruoyi.system.domain;

/**
 * 用户和权限关联表
 */
public class StudentUserRole {
    /**
     * 用户id
     */
    private int userId;
    /**
     * 角色id
     */
    private int roleId;

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
