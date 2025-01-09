package com.ruoyi.system.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface StudentAnswerMapper {
    int updateStudentAnswer(@Param("afterMajor") String afterMajor,
                            @Param("afterAcademy") String afterAcademy,
                            @Param("changeMajorType") Integer changeMajorType,
                            @Param("studentId") String studentId);
}
