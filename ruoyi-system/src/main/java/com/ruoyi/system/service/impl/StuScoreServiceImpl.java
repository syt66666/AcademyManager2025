// 实现类
package com.ruoyi.system.service.impl;

import com.ruoyi.system.domain.StuScore;
import com.ruoyi.system.mapper.StuScoreMapper;
import com.ruoyi.system.service.IStuScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class StuScoreServiceImpl implements IStuScoreService {

    @Autowired
    private StuScoreMapper stuScoreMapper;

    @Transactional
    @Override
    public int addScore(StuScore score) {
        return stuScoreMapper.insert(score);
    }

    @Transactional
    @Override
    public int updateScore(StuScore score) {
        return stuScoreMapper.update(score);
    }

    @Transactional
    @Override
    public int deleteScore(Long scoreId) {
        return stuScoreMapper.deleteById(scoreId);
    }

    @Override
    public StuScore getById(Long scoreId) {
        return stuScoreMapper.selectById(scoreId);
    }

    @Override
    public List<StuScore> listScores(StuScore condition) {
        return stuScoreMapper.selectList(condition);
    }

    @Override
    public List<StuScore> getSemesterScores(String studentId, String semester) {
        return stuScoreMapper.selectByStudentAndSemester(studentId, semester);
    }
}
