package com.ruoyi.system.service;

import com.ruoyi.system.domain.dto.StuCompetitionRecord;

import java.util.List;

public interface StuCompetitionRecordService {

    // 向学生竞赛记录表中插入一条记录
    public void insertStuCompetitionRecord(StuCompetitionRecord record);

    // 获取所有竞赛记录
    // 获取所有竞赛记录
    List<StuCompetitionRecord> getAllCompetitionRecords(StuCompetitionRecord record);
/*
    int deleteStuCompetitionRecord(Integer competitionId);

    int updateStuCompetitionRecord(StuCompetitionRecord record);

    StuCompetitionRecord getStuCompetitionRecordById(Integer competitionId);

    List<StuCompetitionRecord> getAllStuCompetitionRecords();*/
}
