package com.ruoyi.system.controller;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.SemesterStatistics;
import com.ruoyi.system.service.IStuAbilityScoreService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static com.ruoyi.common.core.domain.AjaxResult.success;

@RestController
@RequestMapping("/system/ability")
public class StuAbilityController {
    @Autowired
    private IStuAbilityScoreService abilityScoreService;

    @PostMapping("/refresh")
//    @PreAuthorize("@ss.hasPermi('system:ability:edit')") // 权限控制
    public AjaxResult manualRefresh() {
        abilityScoreService.calculateAndStoreGPAScores();
        return success("数据刷新成功");
    }

    /**
     * 获取学生能力分数（雷达图专用）详细信息
     */
//    @PreAuthorize("@ss.hasPermi('system:score:query')")
    @GetMapping(value = "/{studentId}")
    public AjaxResult getInfo(@PathVariable("studentId") String studentId) {
        return success(abilityScoreService.selectStuAbilityScoreByStudentId(studentId));
    }

    @GetMapping(value = "/count")
    public AjaxResult getCountBySemester(
            @RequestParam("semester") String semester,    // 查询参数 semester
            @RequestParam("studentId") String studentId  // 查询参数 studentId
    ) {
        SemesterStatistics stats = abilityScoreService.selectCountBySemester(semester, studentId);
        return success(stats); // 使用 AjaxResult 包装结果
    }
}
