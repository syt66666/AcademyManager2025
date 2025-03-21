package com.ruoyi.system.service.impl;

import com.ruoyi.common.constant.HttpStatus;
import com.github.pagehelper.PageInfo;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.ServletUtils;
import com.ruoyi.common.utils.ip.IpUtils;
import com.ruoyi.system.domain.AuditHistory;
import com.ruoyi.system.domain.StuCompetitionRecord;
import com.ruoyi.system.domain.StuInfo;
import com.ruoyi.system.domain.StuLectureReport;
import com.ruoyi.system.domain.vo.StuLectureReportVo;
import com.ruoyi.system.mapper.AuditHistoryMapper;
import com.ruoyi.system.mapper.StuInfoMapper;
import com.ruoyi.system.mapper.StuLectureReportMapper;
import com.ruoyi.system.service.IStuLectureReportService;
import com.ruoyi.system.service.ISysUserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.*;
import java.util.stream.Collectors;

import static com.ruoyi.common.utils.PageUtils.startPage;

@Service
public class StuLectureReportServiceImpl implements IStuLectureReportService {
    @Autowired
    private StuInfoMapper stuInfoMapper;
    @Autowired
    private StuLectureReportMapper stuLectureReportMapper;
    @Autowired
    private AuditHistoryMapper auditHistoryMapper;
    @Autowired
    private ISysUserService userService;

    /**
     * 查询学生参与报告信息
     *
     * @param reportId 学生参与报告信息主键
     * @return 学生参与报告信息
     */
    @Override
    public StuLectureReport selectStuLecReportByReportId(Integer reportId)
    {
        return stuLectureReportMapper.selectStuLecReportByReportId(reportId);
    }

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
        // 1. 获取原始状态
        StuLectureReport originalRecord = stuLectureReportMapper
                .selectStuLecReportByReportId(report.getReportId());
        String beforeStatus = originalRecord.getAuditStatusStr();

        // 2. 执行审核状态更新
        int updateResult = stuLectureReportMapper.updateMentorshipAuditInfo(report);
        if (updateResult <= 0) {
            throw new ServiceException("审核状态更新失败");
        }

        // 3. 保存审核记录
        saveAuditHistory(
                report.getReportId(),
                beforeStatus,
                report.getAuditStatusStr(),
                report.getAuditRemark()
        );

        return updateResult;
    }

    // 新增方法：保存审核历史
    private void saveAuditHistory(Integer reportId, String beforeStatus,
                                  String afterStatus, String remark) {
        AuditHistory history = new AuditHistory();

        // 填充模块信息
        history.setModuleType(3);  // 3代表讲座报告模块
        history.setModuleId(reportId.longValue());

        // 状态信息
        history.setAuditStatusBefore(beforeStatus);
        history.setAuditStatusAfter(afterStatus);

        // 操作信息
        history.setAuditAction(getAuditAction(beforeStatus, afterStatus));
        history.setAuditRemark(remark);

        // 用户信息
        SysUser currentUser = userService.selectUserById(SecurityUtils.getUserId());
        history.setAuditorId(currentUser.getUserName());

        // 系统信息
        history.setAuditTime(new Date());
        history.setIpAddress(IpUtils.getIpAddr());
        history.setDeviceInfo(ServletUtils.getRequest().getHeader("User-Agent"));

        // 插入记录
        if (auditHistoryMapper.insertAuditHistory(history) <= 0) {
            throw new ServiceException("审核记录保存失败");
        }
    }

    // 辅助方法：判断审核动作
    private String getAuditAction(String beforeStatus, String afterStatus) {
        if ("已通过".equals(afterStatus)) {
            return "审核通过";
        } else if ("未通过".equals(afterStatus)) {
            return "审核拒绝";
        } else if ("未审核".equals(afterStatus)) {
            return "撤回审核";
        }
        return "状态变更";
    }

    @Override
    public AjaxResult checkUnique(StuLectureReport stuLectureReport) {
        boolean exists = stuLectureReportMapper.existsByUniqueFields(
                stuLectureReport.getStudentId(),
                stuLectureReport.getReportTitle(),
                stuLectureReport.getReporter(),
                stuLectureReport.getReportLocation(),
                stuLectureReport.getReportDate(),
                stuLectureReport.getSemester(),
                stuLectureReport.getAuditStatus()
        );
        return exists ? AjaxResult.error("已存在相同记录") : AjaxResult.success();
    }

    @Override
    public Map<String, Integer> countAuditStatus() {
        try {
            return stuLectureReportMapper.countAuditStatus();
        } catch (Exception e) {
            return new HashMap<String, Integer>(){{
                put("pending", 0);
                put("approved", 0);
                put("rejected", 0);
            }};
        }
    }
}
