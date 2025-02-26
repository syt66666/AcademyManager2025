package com.ruoyi.system.service.impl;

import com.ruoyi.system.domain.StuInfo;
import com.ruoyi.system.domain.StudentLectureReport;
import com.ruoyi.system.mapper.StuInfoMapper;
import com.ruoyi.system.mapper.StuLectureReportMapper;
import com.ruoyi.system.service.StudentLectureReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.security.core.context.SecurityContextHolder;

@Service
public class StudentLectureReportServiceImpl implements StudentLectureReportService {
    @Autowired
    private StuInfoMapper stuInfoMapper;
    @Autowired
    private StuLectureReportMapper stuLectureReportMapper;
    @Override
    public void insertStudentLectureReport(StudentLectureReport studentLectureReport) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String studentId = "";
        if (principal instanceof UserDetails) {
            studentId = ((UserDetails) principal).getUsername();
            // 如果用户名就是用户ID，或者你可以根据用户名查询用户ID
        } else if (principal instanceof String) {
            // 有时principal可能直接是用户名字符串
            studentId = (String) principal;
        }
        StuInfo stuInfo = stuInfoMapper.selectByStudentId(studentId);
        if(stuInfo==null){
            throw new IllegalArgumentException("找不到学生信息");
        }
        studentLectureReport.setStudentId(studentId);
        studentLectureReport.setStudentName(stuInfo.getStudentName());
        int result = stuLectureReportMapper.insertStuLectuerInformation(studentLectureReport);
        if (result <= 0){
            throw new IllegalArgumentException("传入失败");
        }
        System.out.println(studentId);
    }
}
