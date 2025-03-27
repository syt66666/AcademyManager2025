package com.ruoyi.system.controller;


import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.service.impl.MajorSelectionServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
}