package com.ruoyi.system.service.impl;

import com.ruoyi.common.constant.HttpStatus;
import com.github.pagehelper.PageInfo;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.system.domain.StuInfo;
import com.ruoyi.system.domain.StuLectureReport;
import com.ruoyi.system.domain.vo.StuLectureReportVo;
import com.ruoyi.system.mapper.StuInfoMapper;
import com.ruoyi.system.mapper.StuLectureReportMapper;
import com.ruoyi.system.service.IStuLectureReportService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.ruoyi.common.utils.PageUtils.startPage;

@Service
public class StuLectureReportServiceImpl implements IStuLectureReportService {
    @Autowired
    private StuInfoMapper stuInfoMapper;
    @Autowired
    private StuLectureReportMapper stuLectureReportMapper;

    @Override
    public void insertStudentLectureReport(StuLectureReport stuLectureReport) {
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
        stuLectureReport.setStudentId(studentId);
        stuLectureReport.setStudentName(stuInfo.getStudentName());
        //插入学生讲座信息
        int result = stuLectureReportMapper.insertStuLectureInformation(stuLectureReport);
        if (result <= 0) {
            throw new IllegalArgumentException("传入失败");
        }
    }

    /**
     * 获取学生讲座报告信息
     * @return
     */
    @Override
    public TableDataInfo getAllCompetitionRecords(Integer semester, String studentId) {
        startPage();  // 启动分页
        List<StuLectureReport> allLectureReportRecords = stuLectureReportMapper.getAllLectureReportRecords(studentId, semester);
        // 提取分页信息
        PageInfo<StuLectureReport> pageInfo = new PageInfo<>(allLectureReportRecords);
        //拿到总条数
        long total = pageInfo.getTotal();

        List<StuLectureReportVo> lectureReportVoList = allLectureReportRecords.stream().map(record -> {
            StuLectureReportVo vo = new StuLectureReportVo();
            BeanUtils.copyProperties(record, vo);
            return vo;
        }).collect(Collectors.toList());

        // 封装返回数据
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(HttpStatus.SUCCESS);
        rspData.setMsg("查询成功");
        rspData.setRows(lectureReportVoList);  // 当前页数据
        rspData.setTotal(total);  // 总记录数
        return rspData;
    }

    @Override
    public void updateStudentLectureReport(StuLectureReport stuLectureReport) {
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
        int result = stuLectureReportMapper.updateStuLectureInformation(stuLectureReport);
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

    @Override
    public List<StuLectureReport> selectStuLecReportList(StuLectureReport stuLectureReport) {
        return stuLectureReportMapper.selectStuLecReportList(stuLectureReport);
    }

    @Override
    public int updateMentorshipAuditInfo(StuLectureReport report) {
        if (!Arrays.asList(1, 2).contains(report.getAuditStatus())) {
            throw new ServiceException("无效的审核状态");
        }

        // 构建更新参数（仅更新审核相关字段）
        return stuLectureReportMapper.updateMentorshipAuditInfo(report);
    }
}
