package com.ruoyi.system.service;

import com.ruoyi.system.domain.StuCourseInfo;

import java.util.List;

public interface IStuCourseInfoService {
    int addCourse(StuCourseInfo course);
    int updateCourse(StuCourseInfo course);
    int deleteCourseByCode(String courseCode);
    StuCourseInfo getCourseByCode(String courseCode);
    List<StuCourseInfo> getCourseList(StuCourseInfo course);
}
