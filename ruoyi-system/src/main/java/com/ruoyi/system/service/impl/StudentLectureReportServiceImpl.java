package com.ruoyi.system.service.impl;

import com.ruoyi.system.domain.StuInfo;
import com.ruoyi.system.domain.StudentLectureReport;
import com.ruoyi.system.domain.vo.StuLectureReportVo;
import com.ruoyi.system.mapper.StuInfoMapper;
import com.ruoyi.system.mapper.StuLectureReportMapper;
import com.ruoyi.system.service.StudentLectureReportService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudentLectureReportServiceImpl implements StudentLectureReportService {
    @Autowired
    private StuInfoMapper stuInfoMapper;
    @Autowired
    private StuLectureReportMapper stuLectureReportMapper;

    @Override
    public void insertStudentLectureReport(StudentLectureReport studentLectureReport) {
        //获取当前登录用户学号
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String studentId = "";
        if (principal instanceof UserDetails) {
            studentId = ((UserDetails) principal).getUsername();
            // 如果用户名就是用户ID，或者你可以根据用户名查询用户ID
        } else if (principal instanceof String) {
            // 有时principal可能直接是用户名字符串
            studentId = (String) principal;
        }
        //根据学号查找对应学生信息
        StuInfo stuInfo = stuInfoMapper.selectByStudentId(studentId);
        if (stuInfo == null) {
            throw new IllegalArgumentException("找不到学生信息");
        }
        studentLectureReport.setStudentId(studentId);
        studentLectureReport.setStudentName(stuInfo.getStudentName());
        //插入学生讲座信息
        int result = stuLectureReportMapper.insertStuLectureInformation(studentLectureReport);
        if (result <= 0) {
            throw new IllegalArgumentException("传入失败");
        }
    }

    /**
     * 获取学生讲座报告信息
     * @return
     */
    @Override
    public List<StuLectureReportVo> getAllCompetitionRecords() {
        //获取当前登录用户学号
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String studentId = "";
        if (principal instanceof UserDetails) {
            studentId = ((UserDetails) principal).getUsername();
            // 如果用户名就是用户ID，或者你可以根据用户名查询用户ID
        } else if (principal instanceof String) {
            // 有时principal可能直接是用户名字符串
            studentId = (String) principal;
        }
        List<StudentLectureReport> allLectureReportRecords = stuLectureReportMapper.getAllLectureReportRecords(studentId);
        List<StuLectureReportVo> lectureReportVoList = allLectureReportRecords.stream().map(record -> {
            StuLectureReportVo vo = new StuLectureReportVo();
            BeanUtils.copyProperties(record, vo);
            return vo;
        }).collect(Collectors.toList());
        return lectureReportVoList;
    }

    @Override
    public void updateStudentLectureReport(StudentLectureReport studentLectureReport) {
        //获取当前登录用户学号
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String studentId = "";
        if (principal instanceof UserDetails) {
            studentId = ((UserDetails) principal).getUsername();
            // 如果用户名就是用户ID，或者你可以根据用户名查询用户ID
        } else if (principal instanceof String) {
            // 有时principal可能直接是用户名字符串
            studentId = (String) principal;
        }
        if(studentId == null || studentId.isEmpty()){
            throw new IllegalArgumentException("找不到学生信息");
        }

        //更新学生讲座信息
        int result = stuLectureReportMapper.updateStuLectureInformation(studentLectureReport);
        if (result <= 0) {
            throw new IllegalArgumentException("传入失败");
        }
    }

    /**
     * 批量删除学生讲座报告记录
     * @param reportIds
     * @return
     */
    @Override
    public int deleteStuLectureReportByReportIds(Integer[] reportIds) {
        return stuLectureReportMapper.deleteStuLectureReportByReportIds(reportIds);
    }
}
