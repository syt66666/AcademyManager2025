package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.StuMajor;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface StuMajorMapper {
    List<StuMajor> selectByAcademyAndType(
            @Param("major") String major,
            @Param("academy") String academy,
            @Param("type") Integer type
    );
    List<StuMajor> selectChildrenByParentId(Long parentId);
}