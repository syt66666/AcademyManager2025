package com.ruoyi.system.domain;

public class StudentAnswerUpdateRequest {
    private String studentId;
    private String afterMajor;
    private String afterAcademy;
    private Integer changeMajorType;

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
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

    public Integer getChangeMajorType() {
        return changeMajorType;
    }

    public void setChangeMajorType(Integer changeMajorType) {
        this.changeMajorType = changeMajorType;
    }
// Getters and Setters
}