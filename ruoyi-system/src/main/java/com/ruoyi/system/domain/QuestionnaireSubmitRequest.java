package com.ruoyi.system.domain;

public class QuestionnaireSubmitRequest {
    private String userName;
    private Long questionnaireId;
    private String changeAdress;
    private String changeMajor;
    private String afterChangeMajor;
    private String afterChangeAdress;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Long getQuestionnaireId() {
        return questionnaireId;
    }

    public void setQuestionnaireId(Long questionnaireId) {
        this.questionnaireId = questionnaireId;
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

    public String getAfterChangeMajor() {
        return afterChangeMajor;
    }

    public void setAfterChangeMajor(String afterChangeMajor) {
        this.afterChangeMajor = afterChangeMajor;
    }

    public String getAfterChangeAdress() {
        return afterChangeAdress;
    }

    public void setAfterChangeAdress(String afterChangeAdress) {
        this.afterChangeAdress = afterChangeAdress;
    }

    public String getChangeMajorType() {
        return changeMajorType;
    }

    public void setChangeMajorType(String changeMajorType) {
        this.changeMajorType = changeMajorType;
    }

    private String changeMajorType;

    // Getters and Setters
}
