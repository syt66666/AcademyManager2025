package com.ruoyi.system.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.PagesMapper;
import com.ruoyi.system.domain.Pages;
import com.ruoyi.system.service.IPagesService;

/**
 * 问卷信息Service业务层处理
 * 
 * @author ruoyi
 * @date 2024-12-26
 */
@Service
public class PagesServiceImpl implements IPagesService 
{
    @Autowired
    private PagesMapper pagesMapper;

    /**
     * 查询问卷信息
     * 
     * @param id 问卷信息主键
     * @return 问卷信息
     */
    @Override
    public Pages selectPagesById(Long id)
    {
        return pagesMapper.selectPagesById(id);
    }

    /**
     * 查询问卷信息列表
     * 
     * @param pages 问卷信息
     * @return 问卷信息
     */
    @Override
    public List<Pages> selectPagesList(Pages pages)
    {
        return pagesMapper.selectPagesList(pages);
    }

    /**
     * 新增问卷信息
     * 
     * @param pages 问卷信息
     * @return 结果
     */
    @Override
    public int insertPages(Pages pages)
    {
        pages.setCreateTime(DateUtils.getNowDate());
        return pagesMapper.insertPages(pages);
    }

    /**
     * 修改问卷信息
     * 
     * @param pages 问卷信息
     * @return 结果
     */
    @Override
    public int updatePages(Pages pages)
    {
        return pagesMapper.updatePages(pages);
    }

    /**
     * 批量删除问卷信息
     * 
     * @param ids 需要删除的问卷信息主键
     * @return 结果
     */
    @Override
    public int deletePagesByIds(Long[] ids)
    {
        return pagesMapper.deletePagesByIds(ids);
    }

    /**
     * 删除问卷信息信息
     * 
     * @param id 问卷信息主键
     * @return 结果
     */
    @Override
    public int deletePagesById(Long id)
    {
        return pagesMapper.deletePagesById(id);
    }
}
