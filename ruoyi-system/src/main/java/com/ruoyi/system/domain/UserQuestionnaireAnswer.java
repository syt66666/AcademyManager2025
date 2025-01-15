package com.ruoyi.system.domain;

import java.sql.Timestamp;

public class UserQuestionnaireAnswer {
    private Integer id;
    private String userName;
   // private Integer questionnaireId;
    private String academy;
    private String systemMajor;

    private String changeAdress;
    private String changeMajor;
    private String changeMajorType;
    
    private String afterChangeAdress;
    private String afterChangeMajor;
    private String afterChangeMajorType;

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


    public String getAfterChangeMajorType() {
        return afterChangeMajorType;
    }

    public void setAfterChangeMajorType(String afterChangeMajorType) {
        this.afterChangeMajorType = afterChangeMajorType;
    }



    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }


    public String getChangeAdress() {
        return changeAdress;
    }

    public void setChangeAdress(String changeAdress) {
        this.changeAdress = changeAdress;
    }

    public String getChangeMajor() {
        return changeMajor;
    }

    public void setChangeMajor(String changeMajor) {
        this.changeMajor = changeMajor;
    }

    public String getAfterChangeAdress() {
        return afterChangeAdress;
    }

    public void setAfterChangeAdress(String afterChangeAdress) {
        this.afterChangeAdress = afterChangeAdress;
    }

    public String getAfterChangeMajor() {
        return afterChangeMajor;
    }

    public void setAfterChangeMajor(String afterChangeMajor) {
        this.afterChangeMajor = afterChangeMajor;
    }

    public String getChangeMajorType() {
        return changeMajorType;
    }

    public void setChangeMajorType(String changeMajorType) {
        this.changeMajorType = changeMajorType;
    }

}
