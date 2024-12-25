package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.StudentInformation;

import java.util.List;

public interface StudentInformationMapper {
    /**
     * 批量插入学生信息
     */
    public void multipleInsertStudent(List<StudentInformation> studentInformationList);
}
