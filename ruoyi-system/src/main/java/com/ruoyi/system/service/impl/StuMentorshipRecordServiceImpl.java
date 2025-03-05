package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.StuMentorshipRecordMapper;
import com.ruoyi.system.domain.StuMentorshipRecord;
import com.ruoyi.system.service.IStuMentorshipRecordService;


@Service
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
}
