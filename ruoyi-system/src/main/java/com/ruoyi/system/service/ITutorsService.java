package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.Tutors;

/**
 * 导师信息Service接口
 *
 * @author ruoyi
 * @date 2025-03-22
 */
public interface ITutorsService
{
    /**
     * 查询导师信息
     *
     * @param tutorId 导师信息主键
     * @return 导师信息
     */
    public Tutors selectTutorsByTutorId(String tutorId);

    /**
     * 查询导师信息列表
     *
     * @param tutors 导师信息
     * @return 导师信息集合
     */
    public List<Tutors> selectTutorsList(Tutors tutors);

    /**
     * 新增导师信息
     *
     * @param tutors 导师信息
     * @return 结果
     */
    public int insertTutors(Tutors tutors);

    /**
     * 修改导师信息
     *
     * @param tutors 导师信息
     * @return 结果
     */
    public int updateTutors(Tutors tutors);

    /**
     * 批量删除导师信息
     *
     * @param tutorIds 需要删除的导师信息主键集合
     * @return 结果
     */
    public int deleteTutorsByTutorIds(String[] tutorIds);

    /**
     * 删除导师信息信息
     *
     * @param tutorId 导师信息主键
     * @return 结果
     */
    public int deleteTutorsByTutorId(String tutorId);
}
