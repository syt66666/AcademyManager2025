package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.StuScore;
import java.util.List;

public interface StuScoreMapper {
    StuScore selectById(Long scoreId);
    int insert(StuScore entity);
    int update(StuScore entity);
    int deleteById(Long scoreId);
    List<StuScore> selectList(StuScore condition);
    List<StuScore> selectByStudentAndSemester(String studentId, String semester);
}
