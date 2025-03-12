package com.ruoyi.system.service.impl;

import java.util.List;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.StuActivityRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.StuMentorshipRecordMapper;
import com.ruoyi.system.domain.StuMentorshipRecord;
import com.ruoyi.system.service.IStuMentorshipRecordService;


@Service
@Slf4j
public class StuMentorshipRecordServiceImpl implements IStuMentorshipRecordService
{
    @Autowired
    private StuMentorshipRecordMapper stuMentorshipRecordMapper;

    /**
     * 查询导师指导记录
     *
     * @param recordId 导师指导记录主键
     * @return 导师指导记录
     */
    @Override
    public StuMentorshipRecord selectStuMentorshipRecordByRecordId(Integer recordId)
    {
        return stuMentorshipRecordMapper.selectStuMentorshipRecordByRecordId(recordId);
    }

    /**
     * 查询导师指导记录列表
     *
     * @param stuMentorshipRecord 导师指导记录
     * @return 导师指导记录
     */
    @Override
    public List<StuMentorshipRecord> selectStuMentorshipRecordList(StuMentorshipRecord stuMentorshipRecord)
    {
        return stuMentorshipRecordMapper.selectStuMentorshipRecordList(stuMentorshipRecord);
    }

    /**
     * 新增导师指导记录
     *
     * @param stuMentorshipRecord 导师指导记录
     * @return 结果
     */
    @Override
    public int insertStuMentorshipRecord(StuMentorshipRecord stuMentorshipRecord)
    {
        log.info("学生ID：{}",stuMentorshipRecord.getStudentId());
        String tutorId = stuMentorshipRecordMapper.searchTutorId(stuMentorshipRecord.getStudentId());
        log.info("导师ID：{}",tutorId);
        log.info("指导主题：{}",stuMentorshipRecord.getGuidanceTopic());
        log.info("指导时间：{}",stuMentorshipRecord.getGuidanceTime());
        log.info("指导地点：{}",stuMentorshipRecord.getGuidanceLocation());

        stuMentorshipRecord.setTutorId(tutorId);
        return stuMentorshipRecordMapper.insertStuMentorshipRecord(stuMentorshipRecord);
    }

    /**
     * 修改导师指导记录
     *
     * @param stuMentorshipRecord 导师指导记录
     * @return 结果
     */
    @Override
    public int updateStuMentorshipRecord(StuMentorshipRecord stuMentorshipRecord)
    {
        return stuMentorshipRecordMapper.updateStuMentorshipRecord(stuMentorshipRecord);
    }

    /**
     * 批量删除导师指导记录
     *
     * @param recordIds 需要删除的导师指导记录主键
     * @return 结果
     */
    @Override
    public int deleteStuMentorshipRecordByRecordIds(Integer[] recordIds)
    {
        return stuMentorshipRecordMapper.deleteStuMentorshipRecordByRecordIds(recordIds);
    }

    /**
     * 删除导师指导记录信息
     *
     * @param recordId 导师指导记录主键
     * @return 结果
     */
    @Override
    public int deleteStuMentorshipRecordByRecordId(Integer recordId)
    {
        return stuMentorshipRecordMapper.deleteStuMentorshipRecordByRecordId(recordId);
    }

    @Override
    public AjaxResult checkUnique(StuMentorshipRecord stuMentorshipRecord) {
        boolean exists = stuMentorshipRecordMapper.existsByUniqueFields(
                stuMentorshipRecord.getStudentId(),
                stuMentorshipRecord.getGuidanceTopic(),
                stuMentorshipRecord.getGuidanceTime(),
                stuMentorshipRecord.getGuidanceLocation(),
                stuMentorshipRecord.getSemester(),
                stuMentorshipRecord.getAuditStatus()
        );
        return exists ? AjaxResult.error("已存在相同记录") : AjaxResult.success();
    }
}
