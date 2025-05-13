package com.ruoyi.system.controller;


import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.StuMajor;
import com.ruoyi.system.service.impl.MajorSelectionServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/system/major")
public class MajorController extends BaseController {

    @Autowired
    private MajorSelectionServiceImpl selectionService;

    @GetMapping("/tree")
    public AjaxResult getMajorTree(
            @RequestParam("major") String major,
            @RequestParam("academy") String academy,
            @RequestParam("divertForm") String divertForm,
            @RequestParam(name = "innovationStatus", defaultValue = "0") Integer innovationStatus,
            @RequestParam(name = "policyStatus", defaultValue = "0") Integer policyStatus
    ) {

        System.out.println("major: " + major + " academy: " + academy + " divertForm: " + divertForm + " innovationStatus: " + innovationStatus + " policyStatus: " + policyStatus);
        return success(
                selectionService.getAvailableMajors(major, academy, divertForm , innovationStatus, policyStatus)
        );
    }
    /**
     * 获取专业人数统计
     * @param majorId 专业ID (必填)
     * @param isTell 分流来源 (必填)
     * @return 统计结果 {major_name: "专业名", count: 人数}
     */
    @GetMapping("/count")
    public AjaxResult getEveryMajorCount(
            @RequestParam(value = "parent_id", required = true) String parentId,
            @RequestParam(value = "divert_form", required = false, defaultValue = "0") String divertForm,
            @RequestParam(value = "is_tell", required = true) boolean isTell
    ) {
        System.out.println("parentId: " + parentId + " divertForm: " + divertForm + " isTell: " + isTell);
        try {
            int parsedParentId = Integer.parseInt(parentId);
            return success(
                    selectionService.getEveryMajorCount(parsedParentId,isTell,divertForm)
            );
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("专业ID必须为数字");
        }
    }
    //查询不同学院转专业人数
    @GetMapping("/majorNum")
    public AjaxResult echarts2()
    {
        try {
            Map<String, ?> result = selectionService.echarts2();
            return AjaxResult.success(result);
        } catch (Exception e) {
            return AjaxResult.error("获取数据失败: " + e.getMessage());
        }
    }
}