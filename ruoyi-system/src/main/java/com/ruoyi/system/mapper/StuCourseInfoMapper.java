package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.StuCourseInfo;

import java.util.List;

public interface StuCourseInfoMapper {
    int insertStuCourseInfo(StuCourseInfo stuCourseInfo);
    int updateStuCourseInfo(StuCourseInfo stuCourseInfo);
    int deleteStuCourseInfoByCode(String courseCode);
    StuCourseInfo selectStuCourseInfoByCode(String courseCode);
    List<StuCourseInfo> selectStuCourseInfoList(StuCourseInfo stuCourseInfo);
}


