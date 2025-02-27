package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.dto.StuCompetitionRecord;
import org.apache.ibatis.annotations.*;

@Mapper
public interface StuCompetitionRecordMapper {

    int insertStuCompetitionRecord(StuCompetitionRecord record);

  /*  @Delete("DELETE FROM stu_competition_record WHERE competition_id = #{competitionId}")
    int deleteStuCompetitionRecord(@Param("competitionId") Integer competitionId);

    @Update("UPDATE stu_competition_record SET student_id = #{studentId}, competition_name = #{competitionName}, competition_level = #{competitionLevel}, award_level = #{awardLevel}, " +
            "scholarship_points = #{scholarshipPoints}, competition_date = #{competitionDate}, award_date = #{awardDate}, proof_material = #{proofMaterial}, audit_status = #{auditStatus}, " +
            "apply_time = #{applyTime}, nick_name = #{nickName}, audit_time = #{auditTime}, audit_remark = #{auditRemark}, semester = #{semester} WHERE competition_id = #{competitionId}")
    int updateStuCompetitionRecord(StuCompetitionRecord record);

    @Select("SELECT * FROM stu_competition_record WHERE competition_id = #{competitionId}")
    StuCompetitionRecord selectStuCompetitionRecordById(@Param("competitionId") Integer competitionId);

    @Select("SELECT * FROM stu_competition_record")
    List<StuCompetitionRecord> selectAllStuCompetitionRecords();*/
}
