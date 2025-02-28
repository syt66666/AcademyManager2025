package com.ruoyi.system.service.impl;

import com.ruoyi.system.domain.StuCourseInfo;
import com.ruoyi.system.mapper.StuCourseInfoMapper;
import com.ruoyi.system.service.IStuCourseInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StuCourseInfoServiceImpl implements IStuCourseInfoService {

    @Autowired
    private StuCourseInfoMapper stuCourseInfoMapper;

    @Override
    public int addCourse(StuCourseInfo course) {
        return stuCourseInfoMapper.insertStuCourseInfo(course);
    }

    @Override
    public int updateCourse(StuCourseInfo course) {
        return stuCourseInfoMapper.updateStuCourseInfo(course);
    }

    @Override
    public int deleteCourseByCode(String courseCode) {
        return stuCourseInfoMapper.deleteStuCourseInfoByCode(courseCode);
    }

    @Override
    public StuCourseInfo getCourseByCode(String courseCode) {
        return stuCourseInfoMapper.selectStuCourseInfoByCode(courseCode);
    }

    @Override
    public List<StuCourseInfo> getCourseList(StuCourseInfo course) {
        return stuCourseInfoMapper.selectStuCourseInfoList(course);
    }
}
