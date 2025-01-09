package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.StudentInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface StudentInfoMapper {
    StudentInfo selectByStudentId(String studentId);
}

