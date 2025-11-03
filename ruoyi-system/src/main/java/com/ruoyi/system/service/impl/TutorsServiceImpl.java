package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.TutorsMapper;
import com.ruoyi.system.domain.Tutors;
import com.ruoyi.system.service.ITutorsService;

/**
 * 导师信息Service业务层处理
 *
 * @author ruoyi
 * @date 2025-03-22
 */
@Service
public class TutorsServiceImpl implements ITutorsService
{
    @Autowired
    private TutorsMapper tutorsMapper;

    /**
     * 查询导师信息（通过ID主键）
     *
     * @param id 导师信息主键
     * @return 导师信息
     */
    @Override
    public Tutors selectTutorsById(Long id)
    {
        return tutorsMapper.selectTutorsById(id);
    }

    /**
     * 查询导师信息（通过工号）
     *
     * @param tutorId 导师工号
     * @return 导师信息列表（可能有多条，因为同一导师可能属于多个书院）
     */
    @Override
    public List<Tutors> selectTutorsByTutorId(String tutorId)
    {
        return tutorsMapper.selectTutorsByTutorId(tutorId);
    }

    /**
     * 查询导师信息列表
     *
     * @param tutors 导师信息
     * @return 导师信息
     */
    @Override
    public List<Tutors> selectTutorsList(Tutors tutors)
    {
        return tutorsMapper.selectTutorsList(tutors);
    }

    /**
     * 新增导师信息
     *
     * @param tutors 导师信息
     * @return 结果
     */
    @Override
    public int insertTutors(Tutors tutors)
    {
        return tutorsMapper.insertTutors(tutors);
    }

    /**
     * 修改导师信息
     *
     * @param tutors 导师信息
     * @return 结果
     */
    @Override
    public int updateTutors(Tutors tutors)
    {
        return tutorsMapper.updateTutors(tutors);
    }

    /**
     * 删除导师信息（通过ID主键）
     *
     * @param id 导师信息主键
     * @return 结果
     */
    @Override
    public int deleteTutorsById(Long id)
    {
        return tutorsMapper.deleteTutorsById(id);
    }

    /**
     * 删除导师信息（通过工号）
     *
     * @param tutorId 导师工号
     * @return 结果
     */
    @Override
    public int deleteTutorsByTutorId(String tutorId)
    {
        return tutorsMapper.deleteTutorsByTutorId(tutorId);
    }

    /**
     * 批量删除导师信息（通过ID主键）
     *
     * @param ids 需要删除的导师信息主键
     * @return 结果
     */
    @Override
    public int deleteTutorsByIds(Long[] ids)
    {
        return tutorsMapper.deleteTutorsByIds(ids);
    }

    /**
     * 批量删除导师信息（通过工号）
     *
     * @param tutorIds 需要删除的导师工号
     * @return 结果
     */
    @Override
    public int deleteTutorsByTutorIds(String[] tutorIds)
    {
        return tutorsMapper.deleteTutorsByTutorIds(tutorIds);
    }
}
