package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.dto.StuCompetitionRecord;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface StuCompetitionRecordMapper {

    // 向学生竞赛记录表中插入一条记录
    int insertStuCompetitionRecord(StuCompetitionRecord record);

    // 查询所有竞赛记录
    List<StuCompetitionRecord> getAllCompetitionRecords(StuCompetitionRecord record);
}