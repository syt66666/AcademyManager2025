package com.ruoyi.system.mapper;

import java.util.Date;
import java.util.List;
import com.ruoyi.system.domain.StuMentorshipRecord;
import org.apache.ibatis.annotations.Param;


public interface StuMentorshipRecordMapper
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
     * 删除导师指导记录
     *
     * @param recordId 导师指导记录主键
     * @return 结果
     */
    public int deleteStuMentorshipRecordByRecordId(Integer recordId);

    /**
     * 批量删除导师指导记录
     *
     * @param recordIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteStuMentorshipRecordByRecordIds(Integer[] recordIds);

    /**
     * 根据学生ID返回导师ID
     * @param studentId
     * @return 结果
     */
    public String searchTutorId(String studentId);

    public boolean existsByUniqueFields(@Param("studentId") String studentId,
                                        @Param("guidanceTopic") String guidanceTopic,
                                        @Param("guidanceTime") Date guidanceTime,
                                        @Param("guidanceLocation") String guidanceLocation,
                                        @Param("semester") String semester,
                                        @Param("auditStatus") String auditStatus);

}
