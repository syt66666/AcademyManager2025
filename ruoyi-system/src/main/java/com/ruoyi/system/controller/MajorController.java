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

@RestController
@RequestMapping("/system/major")
public class MajorController extends BaseController {

    @Autowired
    private MajorSelectionServiceImpl selectionService;

    @GetMapping("/tree")
    public AjaxResult getMajorTree(
            @RequestParam("major") String major,
            @RequestParam("academy") String academy,
            @RequestParam(name = "innovationStatus", defaultValue = "0") Integer innovationStatus,
            @RequestParam(name = "policyStatus", defaultValue = "0") Integer policyStatus
    ) {

        System.out.println("major: " + major + " academy: " + academy + " innovationStatus: " + innovationStatus + " policyStatus: " + policyStatus);
        return success(
                selectionService.getAvailableMajors(major, academy, innovationStatus, policyStatus)
        );
    }
    /**
     * 获取专业人数统计
     * @param majorId 专业ID (必填)
     * @param divertFrom 分流来源 (必填)
     * @return 统计结果 {major_name: "专业名", count: 人数}
     */
    @GetMapping("/count")
    public AjaxResult getEveryMajorCount(
            @RequestParam(value = "major_id", required = true) String majorId,
            @RequestParam(value = "divert_from", required = true) String divertFrom
    ) {
        System.out.println("majorId: " + majorId + " divertFrom: " + divertFrom);
        try {
            int parsedMajorId = Integer.parseInt(majorId);
            return success(
                    selectionService.getEveryMajorCount(parsedMajorId, divertFrom)
            );
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("专业ID必须为数字");
        }
    }

}