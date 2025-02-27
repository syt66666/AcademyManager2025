package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.StuActivityRecord;

import java.util.List;

public interface StuActivityRecordMapper {
    int insert(StuActivityRecord record);
    int deleteByActivityId(Integer activityId);
    int update(StuActivityRecord record);
    StuActivityRecord selectByActivityId(Integer activityId);
    List<StuActivityRecord> selectAll();
}
