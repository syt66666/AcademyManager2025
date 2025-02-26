package com.ruoyi.system.domain;

public class StuInfo {
    private Integer id;
    private String studentId;
    private String studentName;
    private String academy;
    private String systemMajor;
    private String major;
    private String studentClass;
    private String divertForm;
    private Integer innovationClass;  // 改为包装类型
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

