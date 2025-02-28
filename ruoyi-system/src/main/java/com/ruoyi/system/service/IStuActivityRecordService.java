package com.ruoyi.system.service;

import com.ruoyi.system.domain.StuActivityRecord;
import java.util.List;

public interface IStuActivityRecordService {
    int addStuActivityRecord(StuActivityRecord record);
    int deleteStuActivityRecord(Integer activityId);
    int updateStuActivityRecord(StuActivityRecord record);
    StuActivityRecord getStuActivityRecordById(Integer activityId);
    List<StuActivityRecord> getAllStuActivityRecords();
}
