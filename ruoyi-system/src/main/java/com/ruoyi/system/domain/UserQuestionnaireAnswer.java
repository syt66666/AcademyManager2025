package com.ruoyi.system.domain;

import lombok.Data;

@Data
public class UserQuestionnaireAnswer {
    private Integer id;
    private String userName;
    private Integer questionnaireId;

    private String academy;
    private String originalSystemMajor;

    private String changeAdress;
    private String changeMajor;
    private String changeMajorType;
    
    private String afterChangeAdress;
    private String afterChangeMajor;
    private String afterChangeMajorType;


}
