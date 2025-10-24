package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.TutorStudentRelationMapper;
import com.ruoyi.system.domain.TutorStudentRelation;
import com.ruoyi.system.service.ITutorStudentRelationService;

/**
 * 导师学生关联Service业务层处理
 *
 * @author ruoyi
 * @date 2025-10-24
 */
@Service
public class TutorStudentRelationServiceImpl implements ITutorStudentRelationService
{
    @Autowired
    private TutorStudentRelationMapper tutorStudentRelationMapper;

    /**
     * 查询导师学生关联
     *
     * @param relationId 导师学生关联主键
     * @return 导师学生关联
     */
    @Override
    public TutorStudentRelation selectTutorStudentRelationByRelationId(Long relationId)
    {
        return tutorStudentRelationMapper.selectTutorStudentRelationByRelationId(relationId);
    }

    /**
     * 查询导师学生关联列表
     *
     * @param tutorStudentRelation 导师学生关联
     * @return 导师学生关联
     */
    @Override
    public List<TutorStudentRelation> selectTutorStudentRelationList(TutorStudentRelation tutorStudentRelation)
    {
        return tutorStudentRelationMapper.selectTutorStudentRelationList(tutorStudentRelation);
    }

    /**
     * 新增导师学生关联
     *
     * @param tutorStudentRelation 导师学生关联
     * @return 结果
     */
    @Override
    public int insertTutorStudentRelation(TutorStudentRelation tutorStudentRelation)
    {
        return tutorStudentRelationMapper.insertTutorStudentRelation(tutorStudentRelation);
    }

    /**
     * 修改导师学生关联
     *
     * @param tutorStudentRelation 导师学生关联
     * @return 结果
     */
    @Override
    public int updateTutorStudentRelation(TutorStudentRelation tutorStudentRelation)
    {
        return tutorStudentRelationMapper.updateTutorStudentRelation(tutorStudentRelation);
    }

    /**
     * 批量删除导师学生关联
     *
     * @param relationIds 需要删除的导师学生关联主键
     * @return 结果
     */
    @Override
    public int deleteTutorStudentRelationByRelationIds(Long[] relationIds)
    {
        return tutorStudentRelationMapper.deleteTutorStudentRelationByRelationIds(relationIds);
    }

    /**
     * 删除导师学生关联信息
     *
     * @param relationId 导师学生关联主键
     * @return 结果
     */
    @Override
    public int deleteTutorStudentRelationByRelationId(Long relationId)
    {
        return tutorStudentRelationMapper.deleteTutorStudentRelationByRelationId(relationId);
    }
}
