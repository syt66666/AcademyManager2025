package com.ruoyi.system.service.impl;

import com.ruoyi.system.domain.dto.StuCompetitionRecord;
import com.ruoyi.system.domain.StuInfo;
import com.ruoyi.system.mapper.StuCompetitionRecordMapper;
import com.ruoyi.system.mapper.StuInfoMapper;
import com.ruoyi.system.service.StuCompetitionRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StuCompetitionRecordServiceImpl implements StuCompetitionRecordService {
    @Autowired
    private StuCompetitionRecordMapper mapper;

    @Autowired
    private StuInfoMapper stuInfoMapper;

    // 插入竞赛记录
    @Override
    public void insertStuCompetitionRecord(StuCompetitionRecord record) {
        // 获取当前登录用户的信息
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String studentId = "";

        // 获取学生ID，判断principal的类型
        if (principal instanceof UserDetails) {
            studentId = ((UserDetails) principal).getUsername();
        } else if (principal instanceof String) {
            studentId = (String) principal;
        }

        // 根据学生ID查找学生信息
        StuInfo stuInfo = stuInfoMapper.selectByStudentId(studentId);
        if (stuInfo == null) {
            throw new IllegalArgumentException("找不到学生信息");
        }

        // 设置学生ID和学生姓名到记录中
        record.setStudentId(studentId);
        record.setStudentName(stuInfo.getStudentName());

        // 插入竞赛记录
        int result = mapper.insertStuCompetitionRecord(record);
        if (result <= 0) {
            throw new IllegalArgumentException("插入竞赛记录失败");
        }


   /* // 删除竞赛记录
    public int deleteStuCompetitionRecord(Integer competitionId) {
        return mapper.deleteStuCompetitionRecord(competitionId);
    }

    // 更新竞赛记录
    public int updateStuCompetitionRecord(StuCompetitionRecord record) {
        return mapper.updateStuCompetitionRecord(record);
    }

    // 获取单个竞赛记录
    public StuCompetitionRecord getStuCompetitionRecordById(Integer competitionId) {
        return mapper.selectStuCompetitionRecordById(competitionId);
    }

    // 获取所有竞赛记录
    public List<StuCompetitionRecord> getAllStuCompetitionRecords() {
        return mapper.selectAllStuCompetitionRecords();
    }*/
    }


    // 获取所有竞赛记录
    @Override
    public List<StuCompetitionRecord> getAllCompetitionRecords(StuCompetitionRecord record) {
        return mapper.getAllCompetitionRecords(record);
    }
}
