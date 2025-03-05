package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.StuMentorshipRecord;


public interface IStuMentorshipRecordService
{
    /**
     * 查询导师指导记录
     *
     * @param recordId 导师指导记录主键
     * @return 导师指导记录
     */
    public StuMentorshipRecord selectStuMentorshipRecordByRecordId(Integer recordId);

    /**
     * 查询导师指导记录列表
     *
     * @param stuMentorshipRecord 导师指导记录
     * @return 导师指导记录集合
     */
    public List<StuMentorshipRecord> selectStuMentorshipRecordList(StuMentorshipRecord stuMentorshipRecord);

    /**
     * 新增导师指导记录
     *
     * @param stuMentorshipRecord 导师指导记录
     * @return 结果
     */
    public int insertStuMentorshipRecord(StuMentorshipRecord stuMentorshipRecord);

    /**
     * 修改导师指导记录
     *
     * @param stuMentorshipRecord 导师指导记录
     * @return 结果
     */
    public int updateStuMentorshipRecord(StuMentorshipRecord stuMentorshipRecord);

    /**
     * 批量删除导师指导记录
     *
     * @param recordIds 需要删除的导师指导记录主键集合
     * @return 结果
     */
    public int deleteStuMentorshipRecordByRecordIds(Integer[] recordIds);

    /**
     * 删除导师指导记录信息
     *
     * @param recordId 导师指导记录主键
     * @return 结果
     */
    public int deleteStuMentorshipRecordByRecordId(Integer recordId);
}
