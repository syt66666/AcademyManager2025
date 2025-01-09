package com.ruoyi.system.domain;

public class StudentInfo {
    private int id;
    private String studentId;
    private String studentName;
    private String academy;
    private String systemMajor;
    private String major;
    private String studentClass;
    private String divertForm;
    private int innovationClass;
    private char studentSex;
    private String afterAcademy;
    private String afterMajor;
    private int changeMajorType;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getChangeMajorType() {
        return changeMajorType;
    }

    public void setChangeMajorType(int changeMajorType) {
        this.changeMajorType = changeMajorType;
    }

    public String getAfterMajor() {
        return afterMajor;
    }

    public void setAfterMajor(String afterMajor) {
        this.afterMajor = afterMajor;
    }

    public String getAfterAcademy() {
        return afterAcademy;
    }

    public void setAfterAcademy(String afterAcademy) {
        this.afterAcademy = afterAcademy;
    }

    public char getStudentSex() {
        return studentSex;
    }

    public void setStudentSex(char studentSex) {
        this.studentSex = studentSex;
    }

    public int getInnovationClass() {
        return innovationClass;
    }

    public void setInnovationClass(int innovationClass) {
        this.innovationClass = innovationClass;
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

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getSystemMajor() {
        return systemMajor;
    }

    public void setSystemMajor(String systemMajor) {
        this.systemMajor = systemMajor;
    }

    public String getAcademy() {
        return academy;
    }

    public void setAcademy(String academy) {
        this.academy = academy;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }
}

