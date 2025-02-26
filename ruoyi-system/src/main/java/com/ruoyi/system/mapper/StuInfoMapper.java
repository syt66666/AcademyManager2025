package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.StuInfo;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface StuInfoMapper {
    StuInfo selectByStudentId(String studentId);
}

