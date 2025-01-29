package com.ruoyi.system.domain;

public class QuestionnaireAnswer {
    private String userName;
    private Integer questionnaireId;

    private String changeAdress;
    private String changeMajor;
    private String changeMajorType;

    private String afterChangeAdress;
    private String afterChangeMajor;
    private String afterChangeMajorType;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getQuestionnaireId() {
        return questionnaireId;
    }

    public void setQuestionnaireId(Integer questionnaireId) {
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

    public String getChangeMajorType() {
        return changeMajorType;
    }

    public void setChangeMajorType(String changeMajorType) {
        this.changeMajorType = changeMajorType;
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

    public String getAfterChangeMajorType() {
        return afterChangeMajorType;
    }

    public void setAfterChangeMajorType(String afterChangeMajorType) {
        this.afterChangeMajorType = afterChangeMajorType;
    }
}
