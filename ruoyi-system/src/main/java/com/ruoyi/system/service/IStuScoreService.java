// 接口
package com.ruoyi.system.service;

import com.ruoyi.system.domain.StuScore;
import java.util.List;

public interface IStuScoreService {
    int addScore(StuScore score);
    int updateScore(StuScore score);
    int deleteScore(Long scoreId);
    StuScore getById(Long scoreId);
    List<StuScore> listScores(StuScore condition);
    List<StuScore> getSemesterScores(String studentId, String semester);
}
