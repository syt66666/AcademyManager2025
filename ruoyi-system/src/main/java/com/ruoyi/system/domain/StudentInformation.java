package com.ruoyi.system.domain;

/**
 * 学生信息实体类
 */
public class StudentInformation {
    /**
     * 学生学号
     */
    private String studentId;
    /**
     * 学生姓名
     */
    private String studentName;
    /**
     * 管理部门
     */
    private String studentAddress;
    /**
     * 系统内专业
     */
    private String systemMajor;
    /**
     * 招生录取专业
     */
    private String enrollmentMajor;
    /**
     * 行政班
     */
    private String studentClass;
    /**
     * 备注
     */
    private String studentNote;
    /**
     * 分流形式
     */
    private String studentDiversionForm;
    /**
     * 国家和高校专项计划学生具有域内任选资格
     */
    private boolean haveQualification;
    /**
     * 英文姓名
     */
    private String englishName;
    /**
     * 性别
     */
    private String studentSex;

    /**
     *登录密码（默认123）
     *//*
    private String password;*/

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getStudentSex() {
        return studentSex;
    }

    public void setStudentSex(String studentSex) {
        this.studentSex = studentSex;
    }

    public String getEnglishName() {
        return englishName;
    }

    public void setEnglishName(String englishName) {
        this.englishName = englishName;
    }

    public String getStudentNote() {
        return studentNote;
    }

    public void setStudentNote(String studentNote) {
        this.studentNote = studentNote;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentAddress() {
        return studentAddress;
    }

    public void setStudentAddress(String studentAddress) {
        this.studentAddress = studentAddress;
    }

    public String getSystemMajor() {
        return systemMajor;
    }

    public void setSystemMajor(String systemMajor) {
        this.systemMajor = systemMajor;
    }

    public String getStudentClass() {
        return studentClass;
    }

    public void setStudentClass(String studentClass) {
        this.studentClass = studentClass;
    }

    public String getEnrollmentMajor() {
        return enrollmentMajor;
    }

    public void setEnrollmentMajor(String enrollmentMajor) {
        this.enrollmentMajor = enrollmentMajor;
    }

    public boolean getHaveQualification() {
        return haveQualification;
    }

    public void setHaveQualification(boolean haveQualification) {
        this.haveQualification = haveQualification;
    }

    public String getStudentDiversionForm() {
        return studentDiversionForm;
    }

    public void setStudentDiversionForm(String studentDiversionForm) {
        this.studentDiversionForm = studentDiversionForm;
    }

   /* public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }*/
}
