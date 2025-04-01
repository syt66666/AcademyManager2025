package com.ruoyi.system.service;

public interface IDataChangeService {
    public void notifyDataChange(String majorName,
                                 Integer gradeA,
                                 Integer gradeB,
                                 Integer gradeC,
                                 Integer totalStudents);
}
