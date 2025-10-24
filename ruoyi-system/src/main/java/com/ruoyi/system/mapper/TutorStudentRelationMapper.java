package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.TutorStudentRelation;

/**
 * 导师学生关联Mapper接口
 *
 * @author ruoyi
 * @date 2025-10-24
 */
public interface TutorStudentRelationMapper
{
    /**
     * 查询导师学生关联
     *
     * @param relationId 导师学生关联主键
     * @return 导师学生关联
     */
    public TutorStudentRelation selectTutorStudentRelationByRelationId(Long relationId);

    /**
     * 查询导师学生关联列表
     *
     * @param tutorStudentRelation 导师学生关联
     * @return 导师学生关联集合
     */
    public List<TutorStudentRelation> selectTutorStudentRelationList(TutorStudentRelation tutorStudentRelation);

    /**
     * 新增导师学生关联
     *
     * @param tutorStudentRelation 导师学生关联
     * @return 结果
     */
    public int insertTutorStudentRelation(TutorStudentRelation tutorStudentRelation);

    /**
     * 修改导师学生关联
     *
     * @param tutorStudentRelation 导师学生关联
     * @return 结果
     */
    public int updateTutorStudentRelation(TutorStudentRelation tutorStudentRelation);

    /**
     * 删除导师学生关联
     *
     * @param relationId 导师学生关联主键
     * @return 结果
     */
    public int deleteTutorStudentRelationByRelationId(Long relationId);

    /**
     * 批量删除导师学生关联
     *
     * @param relationIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteTutorStudentRelationByRelationIds(Long[] relationIds);
}
