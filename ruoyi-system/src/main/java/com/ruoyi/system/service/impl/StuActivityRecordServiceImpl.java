package com.ruoyi.system.service.impl;

import com.ruoyi.system.domain.StuActivityRecord;
import com.ruoyi.system.mapper.StuActivityRecordMapper;
import com.ruoyi.system.service.IStuActivityRecordService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class StuActivityRecordServiceImpl implements IStuActivityRecordService {

    private StuActivityRecordMapper stuActivityRecordMapper;

    public StuActivityRecordServiceImpl(StuActivityRecordMapper stuActivityRecordMapper) {
        this.stuActivityRecordMapper = stuActivityRecordMapper;
    }

    @Override
    public int addStuActivityRecord(StuActivityRecord record) {
        return stuActivityRecordMapper.insert(record);
    }

    @Override
    public int deleteStuActivityRecord(Integer activityId) {
        return stuActivityRecordMapper.deleteByActivityId(activityId);
    }

    @Override
    public int updateStuActivityRecord(StuActivityRecord record) {
        return stuActivityRecordMapper.update(record);
    }

    @Override
    public StuActivityRecord getStuActivityRecordById(Integer activityId) {
        return stuActivityRecordMapper.selectByActivityId(activityId);
    }

    @Override
    public List<StuActivityRecord> getAllStuActivityRecords() {
        return stuActivityRecordMapper.selectAll();
    }
}

