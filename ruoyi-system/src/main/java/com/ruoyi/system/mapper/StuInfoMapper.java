package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.StuInfo;
import com.ruoyi.system.domain.UserQuestionnaireAnswer;
import com.ruoyi.system.domain.dto.StuInfoDTO;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


@Mapper
public interface StuInfoMapper {

    /**
     * 查询学生信息列表
     *
     * @param stuInfo 学生信息
     * @return 学生信息集合
     */
    public List<StuInfo> selectStuInfoList(StuInfo stuInfo);

    /**
     * 新增学生信息
     *
     * @param stuInfo 学生信息
     * @return 结果
     */
    public int insertStuInfo(StuInfo stuInfo);

    /**
     * 修改学生信息
     *
     * @param stuInfo 学生信息
     * @return 结果
     */
    public int updateStuInfo(StuInfo stuInfo);

    /**
     * 删除学生信息
     *
     * @param id 学生信息主键
     * @return 结果
     */
    public int deleteStuInfoById(Long id);

    /**
     * 批量删除学生信息
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteStuInfoByIds(Long[] ids);
    /**
     * 查询学生信息列表
     *
     * @param studentId 学生信息
     * @return 学生信息集合
     */
    public StuInfo selectStuInfoById(String studentId);


    @MapKey("academy")  // 指定返回 Map 的键为 academy 字段
    List<StuInfoDTO> getAcademyNum();

    List<StuInfo> getStudentChangeMajorInfo(StuInfo stuInfo);

}

