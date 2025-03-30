package com.ruoyi.system.controller;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.service.IStuAbilityScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/system/ability")
public class StuAbilityController {
    @Autowired
    private IStuAbilityScoreService abilityScoreService;

    @PostMapping("/refresh")
//    @PreAuthorize("@ss.hasPermi('system:ability:edit')") // 权限控制
    public AjaxResult manualRefresh() {
        abilityScoreService.calculateAndStoreGPAScores();
        return AjaxResult.success("数据刷新成功");
    }
}
