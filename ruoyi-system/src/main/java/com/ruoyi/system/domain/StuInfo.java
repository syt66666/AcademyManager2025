package com.ruoyi.system.domain;

public class StuInfo {
    // 学号
    private Integer id;
    // 学生学号
    private String studentId;
    // 学生姓名
    private String studentName;
    // 学院
    private String academy;
    // 系统专业
    private String systemMajor;
    // 专业
    private String major;
    // 学生班级
    private String studentClass;
    // 转专业形式
    private String divertForm;
    // 创新班
    private Integer innovationClass;  // 改为包装类型
    // 学生性别
    private String studentSex;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

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

    public String getAcademy() {
        return academy;
    }

    public void setAcademy(String academy) {
        this.academy = academy;
    }

    public String getSystemMajor() {
        return systemMajor;
    }

    public void setSystemMajor(String systemMajor) {
        this.systemMajor = systemMajor;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getStudentClass() {
        return studentClass;
    }

    public void setStudentClass(String studentClass) {
        this.studentClass = studentClass;
    }

    public String getDivertForm() {
        return divertForm;
    }

    public void setDivertForm(String divertForm) {
        this.divertForm = divertForm;
    }

    public Integer getInnovationClass() {
        return innovationClass;
    }

    public void setInnovationClass(Integer innovationClass) {
        this.innovationClass = innovationClass;
    }

    public String getStudentSex() {
        return studentSex;
    }

    public void setStudentSex(String studentSex) {
        this.studentSex = studentSex;
    }
}

