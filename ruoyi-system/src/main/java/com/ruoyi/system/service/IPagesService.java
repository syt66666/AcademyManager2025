package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.Pages;

/**
 * 问卷信息Service接口
 * 
 * @author ruoyi
 * @date 2024-12-26
 */
public interface IPagesService 
{
    /**
     * 查询问卷信息
     * 
     * @param id 问卷信息主键
     * @return 问卷信息
     */
    public Pages selectPagesById(Long id);

    /**
     * 查询问卷信息列表
     * 
     * @param pages 问卷信息
     * @return 问卷信息集合
     */
    public List<Pages> selectPagesList(Pages pages);

    /**
     * 新增问卷信息
     * 
     * @param pages 问卷信息
     * @return 结果
     */
    public int insertPages(Pages pages);

    /**
     * 修改问卷信息
     * 
     * @param pages 问卷信息
     * @return 结果
     */
    public int updatePages(Pages pages);

    /**
     * 批量删除问卷信息
     * 
     * @param ids 需要删除的问卷信息主键集合
     * @return 结果
     */
    public int deletePagesByIds(Long[] ids);

    /**
     * 删除问卷信息信息
     * 
     * @param id 问卷信息主键
     * @return 结果
     */
    public int deletePagesById(Long id);
}
